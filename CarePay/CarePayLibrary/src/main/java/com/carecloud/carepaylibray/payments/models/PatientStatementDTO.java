package com.carecloud.carepaylibray.payments.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * @author pjohnson on 16/02/18.
 */

public class PatientStatementDTO {

    @Expose
    @SerializedName("metadata")
    private StatementMetadata metadata;

    @Expose
    @SerializedName("statements")
    private List<StatementDTO> statements;

    public StatementMetadata getMetadata() {
        return metadata;
    }

    public void setMetadata(StatementMetadata metadata) {
        this.metadata = metadata;
    }

    public List<StatementDTO> getStatements() {
        return statements;
    }

    public void setStatements(List<StatementDTO> statements) {
        this.statements = statements;
    }
}
