package com.zhb.wms2.util;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * PDF 导出工具。
 *
 * @author zhb
 * @since 2026/4/5
 */
public class PdfExportUtil {

    private static final String FONT_FAMILY = "ArialUnicode";
    private static final String FONT_RESOURCE_PATH = "fonts/ArialUnicode.ttf";

    private static volatile File cachedFontFile;

    private PdfExportUtil() {
    }

    /**
     * 将 HTML 内容渲染为 PDF 并输出到响应流。
     */
    public static void writePdf(String fileName,
                                String title,
                                boolean landscape,
                                String bodyHtml,
                                HttpServletResponse response) throws IOException {
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setContentType("application/pdf");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;filename*=UTF-8''" + encodedFileName);

        try {
            PdfRendererBuilder builder = new PdfRendererBuilder();
            builder.useFastMode();
            builder.useFont(getFontFile(), FONT_FAMILY);
            builder.withHtmlContent(buildHtmlDocument(title, landscape, bodyHtml), null);
            builder.toStream(response.getOutputStream());
            builder.run();
        } catch (Exception ex) {
            throw new IOException("PDF导出失败", ex);
        }
    }

    /**
     * HTML 文本转义。
     */
    public static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    /**
     * 构建完整 PDF HTML 文档。
     */
    private static String buildHtmlDocument(String title, boolean landscape, String bodyHtml) {
        String pageSize = landscape ? "A4 landscape" : "A4";
        return """
                <!DOCTYPE html>
                <html xmlns="http://www.w3.org/1999/xhtml">
                <head>
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
                    <title>%s</title>
                    <style>
                        @page {
                            size: %s;
                            margin: 14mm 12mm;
                        }
                        body {
                            font-family: '%s';
                            font-size: 11px;
                            color: #222222;
                            line-height: 1.5;
                        }
                        h1 {
                            margin: 0 0 12px;
                            font-size: 18px;
                            text-align: center;
                        }
                        h2 {
                            margin: 0 0 8px;
                            font-size: 13px;
                        }
                        table {
                            width: 100%%;
                            border-collapse: collapse;
                            table-layout: fixed;
                            -fs-table-paginate: paginate;
                            -fs-page-break-min-height: 1.5cm;
                        }
                        thead {
                            display: table-header-group;
                        }
                        tr, thead, tfoot {
                            page-break-inside: avoid;
                        }
                        th, td {
                            border: 1px solid #666666;
                            padding: 6px 8px;
                            vertical-align: top;
                            word-break: break-all;
                        }
                        th {
                            background: #f4f4f4;
                            font-weight: 700;
                        }
                        .section {
                            margin-bottom: 14px;
                        }
                        .section-title {
                            margin-bottom: 6px;
                            font-size: 13px;
                            font-weight: 700;
                        }
                        .empty {
                            padding: 20px 0;
                            text-align: center;
                            color: #666666;
                        }
                    </style>
                </head>
                <body>
                    <h1>%s</h1>
                    %s
                </body>
                </html>
                """.formatted(escape(title), pageSize, FONT_FAMILY, escape(title), bodyHtml);
    }

    /**
     * 将类路径中的字体复制到临时文件后复用。
     */
    private static File getFontFile() throws IOException {
        if (cachedFontFile != null && cachedFontFile.exists()) {
            return cachedFontFile;
        }
        synchronized (PdfExportUtil.class) {
            if (cachedFontFile != null && cachedFontFile.exists()) {
                return cachedFontFile;
            }
            ClassPathResource resource = new ClassPathResource(FONT_RESOURCE_PATH);
            if (!resource.exists()) {
                throw new IOException("PDF字体文件不存在: " + FONT_RESOURCE_PATH);
            }
            File tempFile = File.createTempFile("wms2-pdf-font-", ".ttf");
            tempFile.deleteOnExit();
            try (InputStream inputStream = resource.getInputStream()) {
                Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            cachedFontFile = tempFile;
            return tempFile;
        }
    }
}
