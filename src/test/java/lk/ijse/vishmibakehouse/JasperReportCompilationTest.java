package lk.ijse.vishmibakehouse;

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperReport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class JasperReportCompilationTest {

    @Test
    @DisplayName("Verify Compilation of All JasperReports JRXML Templates")
    public void testReportCompilation() throws Exception {
        String[] reports = {
                "/reports/daily_sales_report.jrxml",
                "/reports/ingredient_stock_report.jrxml",
                "/reports/delivery_summary_report.jrxml",
                "/reports/production_summary_report.jrxml"
        };

        for (String rpt : reports) {
            InputStream in = getClass().getResourceAsStream(rpt);
            assertNotNull(in, "Report template stream must exist: " + rpt);
            JasperReport jr = JasperCompileManager.compileReport(in);
            assertNotNull(jr, "Compiled report must not be null for: " + rpt);
            System.out.println("Successfully compiled: " + rpt);
        }
    }
}
