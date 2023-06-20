package ua.gaponov.reports;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Andriy Gaponov
 */
@Slf4j
@Getter
@Setter
public abstract class Report {

    protected String reportText;
    private String name;
    protected Map<String, String> reportParameters = new HashMap<>();

    public Report(String name) {
        this.name = name;
    }

    public abstract void generate();

    public void addParameters(String reportParams){
        String[] params = reportParams.split(";");
        for (String param : params) {
            String[] paramsValues = param.split("=");
            try {
                reportParameters.put(paramsValues[0], paramsValues[1]);
            } catch (Exception e){
                //NOP
            }
        }
    }
}
