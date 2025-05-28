package com.adeem.stockflow.batch.reader;

import com.adeem.stockflow.service.batch.HeaderDetectionService;
import com.adeem.stockflow.service.dto.batch.ProductImportRow;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Iterator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 * Spring Batch ItemReader for reading Excel files and converting rows to ProductImportRow objects.
 */
@Component
public class ExcelProductItemReader extends AbstractItemCountingItemStreamItemReader<ProductImportRow> {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelProductItemReader.class);

    private final HeaderDetectionService headerDetectionService;

    private Resource resource;
    private Workbook workbook;
    private Sheet sheet;
    private Iterator<Row> rowIterator;
    private HeaderDetectionService.HeaderDetectionResult headerResult;
    private int currentRowNumber = 0;
    private int dataRowNumber = 0;
    private Long clientAccountId;

    public ExcelProductItemReader(HeaderDetectionService headerDetectionService) {
        this.headerDetectionService = headerDetectionService;
        setName("excelProductItemReader");
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public void setClientAccountId(Long clientAccountId) {
        this.clientAccountId = clientAccountId;
    }

    @Override
    protected void doOpen() throws Exception {
        LOG.debug("Opening Excel file: {}", resource.getFilename());

        if (resource == null) {
            throw new IllegalStateException("Resource not set");
        }

        // Open the workbook based on file extension
        try (FileInputStream fis = new FileInputStream(resource.getFile())) {
            String filename = resource.getFilename();
            if (filename != null && filename.toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(fis);
            } else if (filename != null && filename.toLowerCase().endsWith(".xls")) {
                workbook = new HSSFWorkbook(fis);
            } else {
                throw new IllegalArgumentException("Unsupported file format. Only .xlsx and .xls files are supported.");
            }
        }

        // Get the first sheet
        sheet = workbook.getSheetAt(0);
        if (sheet == null) {
            throw new IllegalStateException("Excel file does not contain any sheets");
        }

        // Detect headers
        headerResult = headerDetectionService.detectHeaders(sheet);
        if (!headerResult.isSuccessful()) {
            throw new IllegalStateException("Failed to detect headers: " + headerResult.getErrorMessage());
        }

        LOG.debug(
            "Headers detected at row {} with {} columns",
            headerResult.getHeaderRowNumber() + 1,
            headerResult.getColumnMappings().size()
        );

        // Initialize row iterator starting from after the header row
        rowIterator = sheet.iterator();

        // Skip rows up to and including the header row
        for (int i = 0; i <= headerResult.getHeaderRowNumber() && rowIterator.hasNext(); i++) {
            rowIterator.next();
            currentRowNumber++;
        }
    }

    @Override
    protected ProductImportRow doRead() throws Exception {
        if (rowIterator == null || !rowIterator.hasNext()) {
            return null;
        }

        Row row = rowIterator.next();
        currentRowNumber++;
        dataRowNumber++;

        // Skip empty rows
        if (isRowEmpty(row)) {
            return doRead(); // Recursively call to get the next non-empty row
        }

        return convertRowToProductImportRow(row);
    }

    @Override
    protected void doClose() throws Exception {
        LOG.debug("Closing Excel file reader");

        if (workbook != null) {
            try {
                workbook.close();
            } catch (IOException e) {
                LOG.warn("Error closing workbook", e);
            }
        }

        rowIterator = null;
        sheet = null;
        workbook = null;
        headerResult = null;
        currentRowNumber = 0;
        dataRowNumber = 0;
    }

    /**
     * Convert an Excel row to ProductImportRow DTO.
     */
    private ProductImportRow convertRowToProductImportRow(Row row) {
        ProductImportRow importRow = new ProductImportRow(currentRowNumber, dataRowNumber);

        // Map each detected column to the appropriate field
        for (String columnType : headerResult.getDetectedColumnTypes()) {
            Integer columnIndex = headerResult.getColumnIndex(columnType);
            if (columnIndex != null) {
                Cell cell = row.getCell(columnIndex);
                String cellValue = getCellValueAsString(cell);

                setFieldValue(importRow, columnType, cellValue);
            }
        }

        return importRow;
    }

    /**
     * Set field value on ProductImportRow based on column type.
     */
    private void setFieldValue(ProductImportRow importRow, String columnType, String cellValue) {
        if (cellValue == null || cellValue.trim().isEmpty()) {
            return;
        }

        try {
            switch (columnType) {
                case "code":
                    importRow.setCode(cellValue.trim());
                    break;
                case "name":
                    importRow.setName(cellValue.trim());
                    break;
                case "quantity":
                    importRow.setQuantity(parseBigDecimal(cellValue));
                    break;
                case "family":
                    importRow.setFamily(cellValue.trim());
                    break;
                case "category":
                    importRow.setCategory(cellValue.trim());
                    break;
                case "price":
                    importRow.setPrice(parseBigDecimal(cellValue));
                    break;
                case "description":
                    importRow.setDescription(cellValue.trim());
                    break;
                case "manufacturer":
                    importRow.setManufacturer(cellValue.trim());
                    break;
                case "upc":
                    importRow.setUpc(cellValue.trim());
                    break;
                case "manufacturerCode":
                    importRow.setManufacturerCode(cellValue.trim());
                    break;
                case "minimumStockLevel":
                    importRow.setMinimumStockLevel(parseBigDecimal(cellValue));
                    break;
                case "applyTva":
                    importRow.setApplyTva(parseBoolean(cellValue));
                    break;
                case "isVisibleToCustomers":
                    importRow.setIsVisibleToCustomers(parseBoolean(cellValue));
                    break;
                default:
                    LOG.debug("Unknown column type: {}", columnType);
                    break;
            }
        } catch (Exception e) {
            LOG.warn(
                "Error setting field {} with value '{}' at row {}: {}",
                columnType,
                cellValue,
                importRow.getRowNumber(),
                e.getMessage()
            );
        }
    }

    /**
     * Parse BigDecimal from string value.
     */
    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        try {
            // Remove common formatting characters
            String cleanValue = value
                .trim()
                .replace(",", ".") // Handle European decimal separator
                .replace(" ", "") // Remove spaces
                .replaceAll("[^0-9.-]", ""); // Remove non-numeric characters except . and -

            if (cleanValue.isEmpty()) {
                return null;
            }

            return new BigDecimal(cleanValue);
        } catch (NumberFormatException e) {
            LOG.warn("Could not parse BigDecimal from value: '{}'", value);
            return null;
        }
    }

    /**
     * Parse boolean from string value.
     */
    private Boolean parseBoolean(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }

        String cleanValue = value.trim().toLowerCase();

        // True values
        if (
            "true".equals(cleanValue) ||
            "1".equals(cleanValue) ||
            "yes".equals(cleanValue) ||
            "oui".equals(cleanValue) ||
            "sí".equals(cleanValue) ||
            "sim".equals(cleanValue) ||
            "x".equals(cleanValue) ||
            "✓".equals(cleanValue)
        ) {
            return true;
        }

        // False values
        if (
            "false".equals(cleanValue) ||
            "0".equals(cleanValue) ||
            "no".equals(cleanValue) ||
            "non".equals(cleanValue) ||
            "não".equals(cleanValue)
        ) {
            return false;
        }

        return null;
    }

    /**
     * Get cell value as string regardless of cell type.
     */
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return null;
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    // Return as integer if it's a whole number
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    try {
                        double numericValue = cell.getNumericCellValue();
                        if (numericValue == Math.floor(numericValue)) {
                            return String.valueOf((long) numericValue);
                        } else {
                            return String.valueOf(numericValue);
                        }
                    } catch (Exception e2) {
                        return null;
                    }
                }
            case BLANK:
            case _NONE:
            case ERROR:
            default:
                return null;
        }
    }

    /**
     * Check if a row is empty (all cells are null or blank).
     */
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            String cellValue = getCellValueAsString(cell);
            if (cellValue != null && !cellValue.trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Get the header detection result.
     */
    public HeaderDetectionService.HeaderDetectionResult getHeaderResult() {
        return headerResult;
    }

    /**
     * Get current data row number (excluding header).
     */
    public int getDataRowNumber() {
        return dataRowNumber;
    }

    /**
     * Get total number of rows in the sheet.
     */
    public int getTotalRows() {
        return sheet != null ? sheet.getPhysicalNumberOfRows() : 0;
    }

    /**
     * Get estimated data rows (total rows minus header row).
     */
    public int getEstimatedDataRows() {
        if (sheet == null || headerResult == null) {
            return 0;
        }
        return Math.max(0, sheet.getPhysicalNumberOfRows() - headerResult.getHeaderRowNumber() - 1);
    }
}
