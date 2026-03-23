package cn.cyf.chatclient.modules.multimodal.service.impl;

import cn.cyf.chatclient.modules.multimodal.service.DocumentParsingService;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@Service
public class DocumentParsingServiceImpl implements DocumentParsingService {

    @Override
    public String parseDocument(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IllegalArgumentException("File name is null");
        }

        if (fileName.endsWith(".docx")) {
            return parseDocx(file);
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return parseExcel(file);
        } else if (fileName.endsWith(".pdf")) {
            return parsePdf(file);
        } else {
            throw new IllegalArgumentException("Unsupported file type: " + fileName);
        }
    }

    private String parseDocx(MultipartFile file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (InputStream is = file.getInputStream();
             XWPFDocument document = new XWPFDocument(is)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                content.append(paragraph.getText()).append("\n");
            }
        }
        return content.toString();
    }

    private String parseExcel(MultipartFile file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (InputStream is = file.getInputStream();
             Workbook workbook = file.getOriginalFilename().endsWith(".xlsx") ? new XSSFWorkbook(is) : new HSSFWorkbook(is)) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                Sheet sheet = workbook.getSheetAt(i);
                content.append("Sheet: " + sheet.getSheetName()).append("\n");
                for (Row row : sheet) {
                    for (Cell cell : row) {
                        switch (cell.getCellType()) {
                            case STRING:
                                content.append(cell.getStringCellValue()).append("\t");
                                break;
                            case NUMERIC:
                                content.append(cell.getNumericCellValue()).append("\t");
                                break;
                            case BOOLEAN:
                                content.append(cell.getBooleanCellValue()).append("\t");
                                break;
                            default:
                                content.append("\t");
                        }
                    }
                    content.append("\n");
                }
                content.append("\n");
            }
        }
        return content.toString();
    }

    private String parsePdf(MultipartFile file) throws IOException {
        StringBuilder content = new StringBuilder();
        try (InputStream is = file.getInputStream();
             PDDocument document = PDDocument.load(is)) {
            PDFTextStripper stripper = new PDFTextStripper();
            content.append(stripper.getText(document));
        }
        return content.toString();
    }
}