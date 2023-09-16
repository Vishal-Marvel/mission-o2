package com.lrc.missionO2.services;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.lrc.missionO2.DTO.Response.ViewProfileResponse;
import com.lrc.missionO2.entity.Order;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class FileService {

    private void addCell(PdfPTable table, String value){
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(1);
        com.itextpdf.text.Font font = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11);
        cell.setPhrase(new Phrase(value, font));
        table.addCell(cell);
    }
    private void addCell(PdfPTable table, String value, String link){
        PdfPCell cell = new PdfPCell();
        cell.setBorderWidth(1);
        com.itextpdf.text.Font linkText = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 11, com.itextpdf.text.Font.UNDERLINE, BaseColor.BLUE);
        Anchor anchor = new Anchor(new Phrase(value, linkText));
        anchor.setReference(link);
        cell.setPhrase(anchor);
        table.addCell(cell);
    }
    private void startPdf(Document document, String[] headers, PdfPTable table) throws DocumentException, IOException {

        document.open();
        Image image1 = Image.getInstance("Final-LRC-LOGO.png");
        image1.scaleAbsolute(270f, 70f);
        document.add(image1);

        Image image2 = Image.getInstance("Mission-O2-Logo-1.png");
        image2.scaleAbsolute(70f, 70f);
        image2.setAbsolutePosition(650f, 500f);
        document.add(image2);

//        document.setMargins(0, 0, 2, 2);
        Arrays.stream(headers).forEach(col->{
            PdfPCell cell = new PdfPCell();
            cell.setBorderWidth(2);
            cell.setPhrase(new Phrase(col));
            cell.setPaddingRight(col.length());
            table.addCell(cell);
        });
    }
    private int addParameter(Sheet sheet, int currentRow, String parameterName, String parameterValue) {
        if (parameterValue != null) {
            Row row = sheet.createRow(currentRow++);
            Cell parameterNameCell = row.createCell(0);
            parameterNameCell.setCellValue(parameterName);
            Cell parameterValueCell = row.createCell(1);
            parameterValueCell.setCellValue(parameterValue);
        }
        return currentRow;
    }

    private void addHeader(Sheet sheet, int i, Workbook workbook, String[] headers){
        System.out.println(Arrays.toString(headers));
        Row headerRow = sheet.createRow(i);
        CellStyle headerCellStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerCellStyle.setFont(headerFont);

        i = 0;
        for (String field : headers) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(field);
            cell.setCellStyle(headerCellStyle);
            i++;
        }
    }

    public ByteArrayResource createOrdersExcel(
            String[] headers, List<Order> orders, String user, String status, String state, String district, String taluk, Date fromDate, Date toDate) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Orders");
        CellStyle linkStyle = workbook.createCellStyle();
        Font linkText = workbook.createFont();
        linkText.setUnderline(Font.U_SINGLE);
        linkText.setColor(IndexedColors.BLUE.getIndex());
        linkStyle.setFont(linkText);
        int i=0;
        i = addParameter(sheet, i, "Filtered By", "");

        if (user!= null){
            i = addParameter(sheet, i, "User: ", user);
        }
        if (status!= null){
            i = addParameter(sheet, i, "Status: ", status);
        }
        if (fromDate!= null && toDate!=null){
            i = addParameter(sheet, i, "From: ", fromDate.toString().substring(0, 10));
            i = addParameter(sheet, i, "To", toDate.toString().substring(0, 10));
        }
        if (state!= null){
            i = addParameter(sheet, i, "State: ", state);
        }
        if (district!= null){
            i = addParameter(sheet, i, "District: ", district);
        }
        if (taluk!= null){
            i = addParameter(sheet, i, "Taluk: ", taluk);
        }
        i+=1;
        addHeader(sheet, i, workbook, headers);
        i+=1;
        CreationHelper creationHelper = workbook.getCreationHelper(); // For creating hyperlinks

        for (Order order : orders) {
            Row row = sheet.createRow(i++);
            row.createCell(0).setCellValue(order.getOrderNum());
            row.createCell(1).setCellValue(order.getOrderDate().toString().substring(0, 10) + order.getOrderDate().toString().substring(23));
            Cell locationUrlCell = row.createCell(2);
            locationUrlCell.setCellValue("Link");
            Hyperlink link = creationHelper.createHyperlink(HyperlinkType.URL);
            link.setAddress(order.getLocationURL());
            locationUrlCell.setHyperlink(link);
            locationUrlCell.setCellStyle(linkStyle);
            row.createCell(3).setCellValue(order.getState());
            row.createCell(4).setCellValue(order.getTaluk());
            row.createCell(5).setCellValue(order.getDistrict());
            row.createCell(6).setCellValue(order.getOrderStatus());
            row.createCell(7).setCellValue(order.getTotalPrice());
            row.createCell(8).setCellValue(order.getTotalPlants());
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayResource createOrdersPDF(String[] headers, List<Order> orders) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        PdfPTable table = new PdfPTable(headers.length);
        startPdf(document, headers, table);
        for (Order order : orders) {
            addCell(table, order.getOrderNum());
            addCell(table, order.getOrderDate().toString().substring(0, 10) + order.getOrderDate().toString().substring(23));
            addCell(table,  "Link",  order.getLocationURL());
            addCell(table, order.getState());
            addCell(table, order.getTaluk());
            addCell(table, order.getDistrict());
            addCell(table, order.getOrderStatus());
            addCell(table, String.valueOf(order.getTotalPrice()));
            addCell(table, String.valueOf(order.getTotalPlants()));


        }
        document.add(table);
        document.close();
        byte[] pdfBytes = byteArrayOutputStream.toByteArray();

        return new ByteArrayResource(pdfBytes);
    }

    public ByteArrayResource createUsersExcel(String[] headers, List<ViewProfileResponse> users, String state, String district, String taluk, Integer plantCount, Integer orderCount) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Users");
        int i=0;
        i = addParameter(sheet, i, "Filtered By", "");

        if (state!= null){
            i = addParameter(sheet, i, "State: ", state);
        }
        if (district!= null){
            i = addParameter(sheet, i, "District: ", district);
        }
        if (taluk!= null){
            i = addParameter(sheet, i, "Taluk: ", taluk);
        }
        if (plantCount!= null){
            i = addParameter(sheet, i, "Plants Planted: ", String.valueOf(plantCount));
        }
        if (orderCount!= null){
            i = addParameter(sheet, i, "Orders count: ", String.valueOf(orderCount));
        }
        i+=1;
        addHeader(sheet, i, workbook, headers);

        CreationHelper creationHelper = workbook.getCreationHelper(); // For creating hyperlinks


        for (ViewProfileResponse response : users) {
            Row row = sheet.createRow(i++);
            row.createCell(0).setCellValue(response.getName());
            row.createCell(1).setCellValue(response.getAddress().getState());
            row.createCell(2).setCellValue(response.getAddress().getDistrict());
            row.createCell(3).setCellValue(response.getAddress().getTaluk());
            row.createCell(4).setCellValue(response.getTotalPlants());
            row.createCell(5).setCellValue(response.getTotalOrders());
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        workbook.write(byteArrayOutputStream);
        workbook.close();
        return new ByteArrayResource(byteArrayOutputStream.toByteArray());
    }

    public ByteArrayResource createUsersPDF(String[] headers, List<ViewProfileResponse> users, String state, String district, String taluk, Integer plantCount, Integer orderCount) throws DocumentException, IOException {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, byteArrayOutputStream);
        PdfPTable table = new PdfPTable(headers.length);
        startPdf(document, headers, table);
        for (ViewProfileResponse response : users) {
            addCell(table, response.getName());
            addCell(table, response.getAddress().getState());
            addCell(table, response.getAddress().getDistrict());
            addCell(table, response.getAddress().getTaluk());
            addCell(table, String.valueOf(response.getTotalPlants()));
            addCell(table, String.valueOf(response.getTotalOrders()));
        }
        document.add(table);
        document.close();
        byte[] pdfBytes = byteArrayOutputStream.toByteArray();

        return new ByteArrayResource(pdfBytes);
    }

}
