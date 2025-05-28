package com.adeem.stockflow.service.batch;

import java.util.*;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service for intelligent detection of Excel headers in multiple languages.
 */
@Service
public class HeaderDetectionService {

    private static final Logger LOG = LoggerFactory.getLogger(HeaderDetectionService.class);

    // Maximum number of rows to check for headers
    private static final int MAX_HEADER_ROWS = 10;

    // Minimum number of mandatory fields required to consider a row as header
    private static final int MIN_MANDATORY_FIELDS = 3;

    // Column mappings for different languages and variations
    private static final Map<String, Set<String>> COLUMN_MAPPINGS = new HashMap<>();

    static {
        // Code variations
        COLUMN_MAPPINGS.put(
            "code",
            Set.of(
                "code",
                "reference",
                "ref",
                "sku",
                "référence",
                "codigo",
                "product_code",
                "item_code",
                "part_number",
                "part_no",
                "article",
                "codearticle"
            )
        );

        // Name variations
        COLUMN_MAPPINGS.put(
            "name",
            Set.of(
                "name",
                "nom",
                "product_name",
                "produit",
                "nombre",
                "designation",
                "title",
                "nom_produit",
                "product_title",
                "libelle",
                "libellé",
                "description_courte"
            )
        );

        // Quantity variations
        COLUMN_MAPPINGS.put(
            "quantity",
            Set.of(
                "quantity",
                "qty",
                "quantité",
                "stock",
                "cantidad",
                "qte",
                "initial_stock",
                "stock_initial",
                "quantite",
                "qté",
                "quantité_initiale",
                "stock_qty"
            )
        );

        // Family variations
        COLUMN_MAPPINGS.put(
            "family",
            Set.of(
                "family",
                "famille",
                "familia",
                "product_family",
                "group",
                "groupe",
                "category_parent",
                "famille_produit",
                "gamme",
                "line",
                "ligne"
            )
        );

        // Category variations
        COLUMN_MAPPINGS.put(
            "category",
            Set.of("category", "catégorie", "categoria", "type", "product_type", "categorie", "type_produit", "classification", "classe")
        );

        // Price variations
        COLUMN_MAPPINGS.put(
            "price",
            Set.of("price", "prix", "precio", "selling_price", "prix_vente", "unit_price", "prix_unitaire", "cost", "coût", "cout", "tarif")
        );

        // Description variations
        COLUMN_MAPPINGS.put(
            "description",
            Set.of(
                "description",
                "desc",
                "descripción",
                "description_longue",
                "details",
                "détails",
                "notes",
                "commentaire",
                "comment",
                "remarks"
            )
        );

        // Manufacturer variations
        COLUMN_MAPPINGS.put(
            "manufacturer",
            Set.of(
                "manufacturer",
                "fabricant",
                "fabricante",
                "brand",
                "marque",
                "marca",
                "supplier",
                "fournisseur",
                "proveedor",
                "make",
                "maker"
            )
        );

        // UPC variations
        COLUMN_MAPPINGS.put("upc", Set.of("upc", "barcode", "code_barre", "ean", "gtin", "isbn", "asin"));

        // Manufacturer Code variations
        COLUMN_MAPPINGS.put(
            "manufacturerCode",
            Set.of(
                "manufacturer_code",
                "mfg_code",
                "brand_code",
                "supplier_code",
                "code_fabricant",
                "reference_fabricant",
                "part_number",
                "mpn"
            )
        );

        // Minimum Stock Level variations
        COLUMN_MAPPINGS.put(
            "minimumStockLevel",
            Set.of(
                "minimum_stock",
                "min_stock",
                "stock_minimum",
                "seuil_mini",
                "reorder_level",
                "niveau_reapprovisionnement",
                "stock_mini",
                "minimum_qty"
            )
        );

        // Apply TVA variations
        COLUMN_MAPPINGS.put("applyTva", Set.of("apply_tva", "tva", "tax", "taxable", "with_tax", "avec_tva", "impose"));

        // Visible to Customers variations
        COLUMN_MAPPINGS.put(
            "isVisibleToCustomers",
            Set.of("visible", "visible_customers", "public", "visible_client", "active", "enabled")
        );
    }

    /**
     * Detect the header row in an Excel sheet.
     *
     * @param sheet The Excel sheet to analyze
     * @return HeaderDetectionResult containing the header row number and column mappings
     */
    public HeaderDetectionResult detectHeaders(Sheet sheet) {
        LOG.debug("Starting header detection for sheet: {}", sheet.getSheetName());

        if (sheet.getPhysicalNumberOfRows() == 0) {
            return HeaderDetectionResult.notFound("Sheet is empty");
        }

        int maxRowsToCheck = Math.min(MAX_HEADER_ROWS, sheet.getPhysicalNumberOfRows());
        HeaderDetectionResult bestResult = null;
        int bestScore = 0;

        // Check each row as potential header
        for (int rowIndex = 0; rowIndex < maxRowsToCheck; rowIndex++) {
            Row row = sheet.getRow(rowIndex);
            if (row == null) {
                continue;
            }

            HeaderDetectionResult result = analyzeRowAsHeader(row, rowIndex);
            if (result.getScore() > bestScore) {
                bestScore = result.getScore();
                bestResult = result;
            }

            // If we found a very good match, we can stop looking
            if (result.getScore() >= MIN_MANDATORY_FIELDS * 2) {
                LOG.debug("Found excellent header match at row {} with score {}", rowIndex + 1, result.getScore());
                break;
            }
        }

        if (bestResult == null || bestResult.getScore() < MIN_MANDATORY_FIELDS) {
            return HeaderDetectionResult.notFound("No suitable header row found with minimum required fields");
        }

        LOG.debug("Best header row found at row {} with score {}", bestResult.getHeaderRowNumber() + 1, bestResult.getScore());
        return bestResult;
    }

    /**
     * Analyze a specific row to see if it could be a header row.
     */
    private HeaderDetectionResult analyzeRowAsHeader(Row row, int rowIndex) {
        Map<String, Integer> columnMappings = new HashMap<>();
        Map<String, String> detectedColumns = new HashMap<>();
        int score = 0;

        // Check each cell in the row
        for (Cell cell : row) {
            if (cell == null) {
                continue;
            }

            String cellValue = getCellValueAsString(cell);
            if (cellValue == null || cellValue.trim().isEmpty()) {
                continue;
            }

            // Try to match this cell value to known column types
            String matchedColumn = findBestColumnMatch(cellValue);
            if (matchedColumn != null) {
                columnMappings.put(matchedColumn, cell.getColumnIndex());
                detectedColumns.put(matchedColumn, cellValue);

                // Give higher score to mandatory fields
                if (isMandatoryField(matchedColumn)) {
                    score += 2;
                } else {
                    score += 1;
                }
            }
        }

        // Check if we have the minimum required mandatory fields
        boolean hasMandatoryFields = hasMandatoryFields(columnMappings.keySet());

        if (!hasMandatoryFields) {
            score = 0; // Reset score if mandatory fields are missing
        }

        return new HeaderDetectionResult(
            rowIndex,
            columnMappings,
            detectedColumns,
            score,
            hasMandatoryFields,
            hasMandatoryFields ? null : "Missing mandatory fields: code, name, quantity"
        );
    }

    /**
     * Find the best column match for a given cell value.
     */
    private String findBestColumnMatch(String cellValue) {
        String normalizedValue = normalizeString(cellValue);

        // Try exact matches first
        for (Map.Entry<String, Set<String>> entry : COLUMN_MAPPINGS.entrySet()) {
            if (entry.getValue().contains(normalizedValue)) {
                return entry.getKey();
            }
        }

        // Try partial matches for longer headers
        for (Map.Entry<String, Set<String>> entry : COLUMN_MAPPINGS.entrySet()) {
            for (String pattern : entry.getValue()) {
                if (normalizedValue.contains(pattern) || pattern.contains(normalizedValue)) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    /**
     * Normalize string for comparison (lowercase, remove spaces, accents, etc.)
     */
    private String normalizeString(String input) {
        if (input == null) {
            return "";
        }

        return input
            .toLowerCase()
            .replaceAll("\\s+", "_")
            .replaceAll("[àáâãäå]", "a")
            .replaceAll("[èéêë]", "e")
            .replaceAll("[ìíîï]", "i")
            .replaceAll("[òóôõö]", "o")
            .replaceAll("[ùúûü]", "u")
            .replaceAll("[ñ]", "n")
            .replaceAll("[ç]", "c")
            .replaceAll("[^a-z0-9_]", "");
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
                // Check if it's a date
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Format as integer if it's a whole number
                    double numericValue = cell.getNumericCellValue();
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
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return null;
        }
    }

    /**
     * Check if we have all mandatory fields.
     */
    private boolean hasMandatoryFields(Set<String> detectedColumns) {
        return detectedColumns.contains("code") && detectedColumns.contains("name") && detectedColumns.contains("quantity");
    }

    /**
     * Check if a field is mandatory.
     */
    private boolean isMandatoryField(String field) {
        return "code".equals(field) || "name".equals(field) || "quantity".equals(field);
    }

    /**
     * Get supported column variations for documentation/help.
     */
    public Map<String, Set<String>> getSupportedColumnVariations() {
        return new HashMap<>(COLUMN_MAPPINGS);
    }

    /**
     * Result of header detection.
     */
    public static class HeaderDetectionResult {

        private final int headerRowNumber;
        private final Map<String, Integer> columnMappings;
        private final Map<String, String> detectedColumns;
        private final int score;
        private final boolean successful;
        private final String errorMessage;

        public HeaderDetectionResult(
            int headerRowNumber,
            Map<String, Integer> columnMappings,
            Map<String, String> detectedColumns,
            int score,
            boolean successful,
            String errorMessage
        ) {
            this.headerRowNumber = headerRowNumber;
            this.columnMappings = columnMappings != null ? columnMappings : new HashMap<>();
            this.detectedColumns = detectedColumns != null ? detectedColumns : new HashMap<>();
            this.score = score;
            this.successful = successful;
            this.errorMessage = errorMessage;
        }

        public static HeaderDetectionResult notFound(String errorMessage) {
            return new HeaderDetectionResult(-1, null, null, 0, false, errorMessage);
        }

        // Getters
        public int getHeaderRowNumber() {
            return headerRowNumber;
        }

        public Map<String, Integer> getColumnMappings() {
            return columnMappings;
        }

        public Map<String, String> getDetectedColumns() {
            return detectedColumns;
        }

        public int getScore() {
            return score;
        }

        public boolean isSuccessful() {
            return successful;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public boolean hasColumn(String column) {
            return columnMappings.containsKey(column);
        }

        public Integer getColumnIndex(String column) {
            return columnMappings.get(column);
        }

        public String getDetectedColumnName(String column) {
            return detectedColumns.get(column);
        }

        public Set<String> getDetectedColumnTypes() {
            return columnMappings.keySet();
        }

        @Override
        public String toString() {
            return (
                "HeaderDetectionResult{" +
                "headerRowNumber=" +
                headerRowNumber +
                ", columnMappings=" +
                columnMappings +
                ", detectedColumns=" +
                detectedColumns +
                ", score=" +
                score +
                ", successful=" +
                successful +
                ", errorMessage='" +
                errorMessage +
                "'" +
                "}"
            );
        }
    }
}
