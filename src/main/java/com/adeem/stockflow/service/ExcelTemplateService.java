package com.adeem.stockflow.service;

import com.adeem.stockflow.domain.enumeration.ProductCategory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Service for generating Excel templates for product import.
 */
@Service
public class ExcelTemplateService {

    private static final Logger LOG = LoggerFactory.getLogger(ExcelTemplateService.class);

    /**
     * Generate Excel template with sample data and instructions.
     */
    public Resource generateTemplate() {
        LOG.debug("Generating Excel template for product import");

        try (Workbook workbook = new XSSFWorkbook()) {
            // Create main template sheet
            createTemplateSheet(workbook);

            // Create instructions sheet
            createInstructionsSheet(workbook);

            // Create categories reference sheet
            createCategoriesSheet(workbook);

            // Convert to byte array
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);

            return new ByteArrayResource(outputStream.toByteArray());
        } catch (IOException e) {
            LOG.error("Error generating Excel template", e);
            throw new RuntimeException("Failed to generate template", e);
        }
    }

    private void createTemplateSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Product Import Template");

        // Create cell styles
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle sampleStyle = createSampleStyle(workbook);

        // Create title row
        Row titleRow = sheet.createRow(0);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("StockFlow Product Import Template");
        titleCell.setCellStyle(createTitleStyle(workbook));

        // Empty row
        sheet.createRow(1);

        // Create header row
        Row headerRow = sheet.createRow(2);
        String[] headers = {
            "Code*",
            "Name*",
            "Quantity*",
            "Family",
            "Category",
            "Price",
            "Description",
            "Manufacturer",
            "UPC",
            "Manufacturer Code",
            "Min Stock Level",
            "Apply TVA",
            "Visible to Customers",
        };

        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // Create sample data rows
        createSampleDataRow(
            sheet,
            3,
            sampleStyle,
            "LAPTOP001",
            "Gaming Laptop",
            "5",
            "Electronics",
            "COMPUTERS",
            "1299.99",
            "High-performance gaming laptop",
            "ASUS",
            "123456789012",
            "ROG001",
            "1",
            "FALSE",
            "TRUE"
        );
        createSampleDataRow(
            sheet,
            4,
            sampleStyle,
            "MOUSE001",
            "Wireless Mouse",
            "25",
            "Electronics",
            "ACCESSORIES",
            "29.99",
            "Ergonomic wireless mouse",
            "Logitech",
            "987654321098",
            "MX001",
            "5",
            "TRUE",
            "TRUE"
        );
        createSampleDataRow(
            sheet,
            5,
            sampleStyle,
            "BOOK001",
            "Spring Boot Guide",
            "10",
            "Education",
            "BOOKS",
            "45.00",
            "Comprehensive Spring Boot tutorial",
            "TechBooks",
            "",
            "SB2024",
            "2",
            "FALSE",
            "TRUE"
        );

        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        // Set column widths (minimum widths)
        for (int i = 0; i < headers.length; i++) {
            int currentWidth = sheet.getColumnWidth(i);
            sheet.setColumnWidth(i, Math.max(currentWidth, 3000));
        }
    }

    private void createInstructionsSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Instructions");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle textStyle = createTextStyle(workbook);

        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Product Import Instructions");
        titleCell.setCellStyle(titleStyle);
        rowNum++; // Empty row

        // Instructions
        String[] instructions = {
            "MANDATORY FIELDS (marked with *):",
            "• Code: Unique product identifier (must be unique in your account)",
            "• Name: Product name or title",
            "• Quantity: Initial stock quantity (must be a positive number)",
            "",
            "OPTIONAL FIELDS:",
            "• Family: Product family name (will be created if doesn't exist)",
            "• Category: Product category (see Categories sheet for valid values)",
            "• Price: Selling price (positive number)",
            "• Description: Product description",
            "• Manufacturer: Manufacturer or brand name",
            "• UPC: Universal Product Code (barcode)",
            "• Manufacturer Code: Manufacturer's product code",
            "• Min Stock Level: Minimum stock level for alerts (positive number)",
            "• Apply TVA: TRUE/FALSE for tax application",
            "• Visible to Customers: TRUE/FALSE for customer visibility",
            "",
            "SUPPORTED COLUMN NAMES:",
            "The system supports multiple languages and variations:",
            "• Code: code, reference, ref, sku, référence, codigo",
            "• Name: name, nom, product_name, produit, designation",
            "• Quantity: quantity, qty, quantité, stock, qte",
            "• Family: family, famille, product_family, group",
            "• Category: category, catégorie, type, product_type",
            "",
            "TIPS:",
            "• Headers can be in row 1-10 (system will auto-detect)",
            "• Empty rows will be skipped",
            "• Duplicate product codes will be rejected",
            "• Invalid categories will default to 'MISC'",
            "• Boolean values: TRUE/FALSE, 1/0, YES/NO, OUI/NON",
            "",
            "SUPPORTED FILE FORMATS:",
            "• Excel 2007+ (.xlsx)",
            "• Excel 97-2003 (.xls)",
            "• Maximum file size: 50MB",
        };

        for (String instruction : instructions) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(instruction);
            if (instruction.endsWith(":")) {
                cell.setCellStyle(headerStyle);
            } else {
                cell.setCellStyle(textStyle);
            }
        }

        // Auto-size column
        sheet.autoSizeColumn(0);
        sheet.setColumnWidth(0, Math.max(sheet.getColumnWidth(0), 15000));
    }

    private void createCategoriesSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Categories");

        CellStyle titleStyle = createTitleStyle(workbook);
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle textStyle = createTextStyle(workbook);

        int rowNum = 0;

        // Title
        Row titleRow = sheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Supported Product Categories");
        titleCell.setCellStyle(titleStyle);
        rowNum++; // Empty row

        // Header
        Row headerRow = sheet.createRow(rowNum++);
        Cell headerCell = headerRow.createCell(0);
        headerCell.setCellValue("Category Name");
        headerCell.setCellStyle(headerStyle);

        // Categories
        for (ProductCategory category : ProductCategory.values()) {
            Row row = sheet.createRow(rowNum++);
            Cell cell = row.createCell(0);
            cell.setCellValue(category.name());
            cell.setCellStyle(textStyle);
        }

        // Auto-size column
        sheet.autoSizeColumn(0);
    }

    private void createSampleDataRow(Sheet sheet, int rowNum, CellStyle style, String... values) {
        Row row = sheet.createRow(rowNum);
        for (int i = 0; i < values.length; i++) {
            Cell cell = row.createCell(i);
            cell.setCellValue(values[i]);
            cell.setCellStyle(style);
        }
    }

    private CellStyle createTitleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 16);
        font.setColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFont(font);
        return style;
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        font.setColor(IndexedColors.WHITE.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }

    private CellStyle createSampleStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setFillForegroundColor(IndexedColors.LIGHT_YELLOW.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return style;
    }

    private CellStyle createTextStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setWrapText(true);
        return style;
    }
}
