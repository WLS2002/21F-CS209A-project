package com.example.java2project.data;

import java.util.HashMap;
import java.util.Map;

public class Info {

    private final Map<String, String> dataDict;
    private final String[] fields;

    public Info(String[] fields, String rawInfo){
        rawInfo = rawInfo.replace("occupied Palestinian territory, including east Jerusalem", "occupied Palestinian territory including east Jerusalem");
        this.fields = fields;
        dataDict = new HashMap<>();
        rawInfo = rawInfo + ",temp";
        String[] values = rawInfo.split(",");
        for (int i = 0; i < fields.length; i++){
            dataDict.put(fields[i], values[i].equals("") ? "0" : values[i]);
        }
    }

    public Info(String[] fields, Map<String, String> data){
        this.fields = fields;
        this.dataDict = data;
    }

    public String getValue(String valName) throws Exception {
        if(!dataDict.containsKey(valName))
            throw new Exception("Can not find this valName");
        return dataDict.get(valName);
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (String field : fields) {
            String val = dataDict.get(field);
            if (val == null)
                val = "";
            sb.append(val).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public Map<String, String> getDataDict(){
        return dataDict;
    }

}
