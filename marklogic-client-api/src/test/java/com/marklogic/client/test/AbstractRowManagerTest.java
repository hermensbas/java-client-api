package com.marklogic.client.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.marklogic.client.expression.PlanBuilder;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.JacksonHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.row.RawPlanDefinition;
import com.marklogic.client.row.RowManager;
import com.marklogic.client.row.RowRecord;
import org.junit.Before;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public abstract class AbstractRowManagerTest {

    protected RowManager rowManager;
    protected PlanBuilder op;
    protected ObjectMapper mapper = new ObjectMapper();

    @Before
    public void beforeClass() {
        Common.connect();
        // Subclasses of this test are expected to only write URIs starting with /fromParam/, so delete all of them
        // before running the test to ensure a document doesn't already exist.
        Common.connectServerAdmin().newServerEval()
                .xquery("cts:uri-match('/fromParam/*') ! xdmp:document-delete(.)")
                .evalAs(String.class);

        rowManager = Common.client.newRowManager();
        op = rowManager.newPlanBuilder();
    }

    protected void verifyExportedPlanReturnsSameRowCount(PlanBuilder.ExportablePlan plan) {
        verifyExportedPlanReturnsSameRowCount(plan, null);
    }

    protected final void verifyExportedPlanReturnsSameRowCount(PlanBuilder.ExportablePlan plan,
                                                               Function<PlanBuilder.Plan, PlanBuilder.Plan> bindingFunction) {
        PlanBuilder.Plan planToExecute = bindingFunction != null ? bindingFunction.apply(plan) : plan;
        List<RowRecord> rowsFromPlan = rowManager
                .resultRows(planToExecute)
                .stream().collect(Collectors.toList());

        String exportedPlan = plan.exportAs(String.class);
        RawPlanDefinition rawPlan = rowManager.newRawPlanDefinition(new StringHandle(exportedPlan));
        PlanBuilder.Plan rawPlanToExecute = bindingFunction != null ? bindingFunction.apply(rawPlan) : rawPlan;

        List<RowRecord> rowsFromExportedPlan = rowManager
                .resultRows(rawPlanToExecute)
                .stream().collect(Collectors.toList());
        assertEquals("The row count from the exported list should match that of the rows from the original plan",
                rowsFromPlan.size(), rowsFromExportedPlan.size());
    }

    protected final void verifyJsonDoc(String uri, Consumer<ObjectNode> verifier) {
        verifier.accept((ObjectNode) Common.client.newJSONDocumentManager().read(uri, new JacksonHandle()).get());
    }

    protected final void verifyMetadata(String uri, Consumer<DocumentMetadataHandle> verifier) {
        verifier.accept(Common.client.newJSONDocumentManager().readMetadata(uri, new DocumentMetadataHandle()));
    }

    protected final String getRowContentWithoutXmlDeclaration(RowRecord row, String columnName) {
        String content = row.getContentAs(columnName, String.class);
        return content.replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n", "");
    }
}
