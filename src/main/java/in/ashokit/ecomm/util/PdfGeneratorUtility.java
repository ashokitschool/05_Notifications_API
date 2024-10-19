package in.ashokit.ecomm.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import in.ashokit.ecomm.entity.Order;
import in.ashokit.ecomm.entity.OrderItem;

@Component
public class PdfGeneratorUtility {

	@Value("${pdfDir}")
	private String pdfDir;

	@Value("${reportFileName}")
	private String reportFileName;

	@Value("${reportFileNameDateFormat}")
	private String reportFileNameDateFormat;

	@Value("${localDateFormat}")
	private String localDateFormat;

	@Value("${logoImgPath}")
	private String logoImgPath;

	@Value("${logoImgScale}")
	private Float[] logoImgScale;

	@Value("${currencySymbol:}")
	private String currencySymbol;

	@Value("${table_noOfColumns}")
	private int noOfColumns;

	@Value("${table.columnNames}")
	private List<String> columnNames;

	private static Font COURIER = new Font(Font.FontFamily.COURIER, 20, Font.BOLD);
	private static Font COURIER_SMALL = new Font(Font.FontFamily.COURIER, 16, Font.BOLD);
	private static Font COURIER_SMALL_FOOTER = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);

	private static final Path root = Paths.get("invoices");

	@Autowired
	private S3Util s3Util;

	public String generatePdfReport(Order order) {

		try {
			if(!Files.exists(root))
				Files.createDirectories(root);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Document document = new Document();

		try {
			if(Objects.nonNull(order)) {
				String fileName = getPdfNameWithDate(order.getOrderTrackingNum());
				System.out.println("fileName "+fileName);
				FileOutputStream os = new FileOutputStream(fileName);
				PdfWriter.getInstance(document, os);
				document.open();
				addLogo(document);
				addDocTitle(document);
				addCusomerInfo(document, order);
				addOrderInfo(document, order);
				createTable(document, noOfColumns, order, fileName);
				addFooter(document);
				document.close();
				// move file to s3 bucket
				String invoiceUrl = s3Util.saveFileInBucket(new File(fileName));
				System.out.println("------------------Invoice is ready!-------------------------");
				return invoiceUrl;
			}

		} catch (FileNotFoundException | DocumentException e) {
			e.printStackTrace();
		}
		return null;

	}

	private void addLogo(Document document) {
		try {	
			System.out.println("root "+root);
			Image img = Image.getInstance(root+"/"+logoImgPath);
			img.scalePercent(logoImgScale[0], logoImgScale[1]);
			img.setAlignment(Element.ALIGN_RIGHT);
			document.add(img);
		} catch (DocumentException | IOException e) {
			e.printStackTrace();
		}
	}

	private void addDocTitle(Document document) throws DocumentException {
		//String localDateString = LocalDateTime.now().format(DateTimeFormatter.ofPattern(localDateFormat));
		Paragraph p1 = new Paragraph();
		leaveEmptyLine(p1, 1);
		p1.add(new Paragraph(StringUtils.capitalize(reportFileName), COURIER));
		p1.setAlignment(Element.ALIGN_MIDDLE);
		leaveEmptyLine(p1, 1);
		//p1.add(new Paragraph("Invoice generated on " + localDateString, COURIER_SMALL));
		p1.add(new Paragraph("This is a system generated invoice, signature is not required", COURIER_SMALL));
		document.add(p1);

	}

	private void addOrderInfo(Document document, Order order) throws DocumentException {
		Paragraph p1 = new Paragraph();
		leaveEmptyLine(p1, 1);
		p1.add(new Paragraph("Order Number : "+order.getOrderTrackingNum(), COURIER));
		p1.setAlignment(Element.ALIGN_LEFT);
		leaveEmptyLine(p1, 1);
		p1.add(new Paragraph("Order Date : " + order.getDateCreated(), COURIER_SMALL));

		document.add(p1);

	}

	private void addCusomerInfo(Document document, Order order) throws DocumentException {
		Paragraph p1 = new Paragraph();
		leaveEmptyLine(p1, 1);
		p1.add(new Paragraph("Customer Name : "+order.getCustomer().getName(), COURIER));
		p1.setAlignment(Element.ALIGN_LEFT);
		leaveEmptyLine(p1, 1);
		p1.add(new Paragraph("Mobile : " + order.getCustomer().getPhno(), COURIER_SMALL));
		leaveEmptyLine(p1, 1);
		p1.add(new Paragraph("Email : " + order.getCustomer().getEmail(), COURIER_SMALL));
		leaveEmptyLine(p1, 2);
		document.add(p1);

		Paragraph p2 = new Paragraph();
		leaveEmptyLine(p2, 1);
		p2.add(new Paragraph("Billing Address :", COURIER));
		p2.setAlignment(Element.ALIGN_RIGHT);
		leaveEmptyLine(p2, 1);
		p2.add(new Paragraph(order.getAddress().getStreet(), COURIER_SMALL));
		leaveEmptyLine(p2, 1);
		p2.add(new Paragraph(order.getAddress().getCity(), COURIER_SMALL));
		leaveEmptyLine(p2, 1);
		p2.add(new Paragraph(order.getAddress().getState(), COURIER_SMALL));
		leaveEmptyLine(p2, 1);
		p2.add(new Paragraph(order.getAddress().getZipCode(), COURIER_SMALL));
		document.add(p2);

		Paragraph p3 = new Paragraph();
		leaveEmptyLine(p3, 1);
		p3.add(new Paragraph("Shipping Address :", COURIER));
		p3.setAlignment(Element.ALIGN_RIGHT);
		leaveEmptyLine(p3, 1);
		p3.add(new Paragraph("same as Billing Address", COURIER_SMALL));
		leaveEmptyLine(p3, 1);
		document.add(p3);

	}

	private void createTable(Document document, int noOfColumns, Order order, String fileName) throws DocumentException {
		Paragraph paragraph = new Paragraph();
		leaveEmptyLine(paragraph, 3);
		document.add(paragraph);

		PdfPTable table = new PdfPTable(noOfColumns);

		for(int i=0; i<noOfColumns; i++) {
			PdfPCell cell = new PdfPCell(new Phrase(columnNames.get(i)));
			cell.setHorizontalAlignment(Element.ALIGN_CENTER);
			cell.setBackgroundColor(BaseColor.CYAN);
			table.addCell(cell);
		}

		table.setHeaderRows(1);
		prepateItems(table, order, fileName);
		document.add(table);
	}

	private void prepateItems(PdfPTable table, Order order, String fileName) {

		List<OrderItem> items = order.getItems();
		for (OrderItem item : items) {
			table.setWidthPercentage(100);
			table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
			table.getDefaultCell().setVerticalAlignment(Element.ALIGN_MIDDLE);
			table.addCell(item.getProdname());
			table.addCell(String.valueOf(item.getQuantity()));
			table.addCell(currencySymbol+item.getUnitPrice());
			BigDecimal netPrice = new BigDecimal(item.getUnitPrice() * item.getQuantity()).setScale(2, RoundingMode.HALF_UP);
			table.addCell(currencySymbol + netPrice.doubleValue());
		}
		table.addCell("");
		table.addCell("");
		Font boldFont = new Font(Font.FontFamily.COURIER, 12, Font.BOLD);
		Phrase totalPriceField = new Phrase("Total Price", boldFont );
		table.addCell(totalPriceField);
		BigDecimal totalPrice = new BigDecimal(order.getTotalPrice()).setScale(2, RoundingMode.HALF_UP);
		table.addCell(currencySymbol + totalPrice);
	}

	private void addFooter(Document document) throws DocumentException {
		Paragraph p2 = new Paragraph();
		leaveEmptyLine(p2, 3);
		p2.setAlignment(Element.ALIGN_MIDDLE);
		p2.add(new Paragraph(
				"------------------------End Of " +reportFileName+"------------------------", 
				COURIER_SMALL_FOOTER));

		document.add(p2);
	}

	private static void leaveEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}

	private String getPdfNameWithDate(String orderTrackingNumber) {
		return pdfDir+reportFileName+"-"+orderTrackingNumber+".pdf";
	}

}
