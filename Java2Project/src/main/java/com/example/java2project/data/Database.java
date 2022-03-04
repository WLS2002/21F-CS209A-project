package com.example.java2project.data;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

//{"iso_code", "continent", "location", "date", "total_cases", "new_cases", "new_cases_smoothed", "total_deaths", "new_deaths", "new_deaths_smoothed", "total_cases_per_million", "new_cases_per_million", "new_cases_smoothed_per_million", "total_deaths_per_million", "new_deaths_per_million", "new_deaths_smoothed_per_million", "reproduction_rate", "icu_patients", "icu_patients_per_million", "hosp_patients", "hosp_patients_per_million", "weekly_icu_admissions", "weekly_icu_admissions_per_million", "weekly_hosp_admissions", "weekly_hosp_admissions_per_million", "new_tests", "total_tests", "total_tests_per_thousand", "new_tests_per_thousand", "new_tests_smoothed", "new_tests_smoothed_per_thousand", "positive_rate", "tests_per_case", "tests_units", "total_vaccinations", "people_vaccinated", "people_fully_vaccinated", "total_boosters", "new_vaccinations", "new_vaccinations_smoothed", "total_vaccinations_per_hundred", "people_vaccinated_per_hundred", "people_fully_vaccinated_per_hundred", "total_boosters_per_hundred", "new_vaccinations_smoothed_per_million", "stringency_index", "population", "population_density", "median_age", "aged_65_older", "aged_70_older", "gdp_per_capita", "extreme_poverty", "cardiovasc_death_rate", "diabetes_prevalence", "female_smokers", "male_smokers", "handwashing_facilities", "hospital_beds_per_thousand", "life_expectancy", "human_development_index", "excess_mortality_cumulative_absolute", "excess_mortality_cumulative", "excess_mortality", "excess_mortality_cumulative_per_million",};

public class Database {

    private List<Info> infoList;
    private String[] fields;

    public static Database loadFile(String csvFilePath) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(csvFilePath)));
        List<String> lines = new ArrayList<>();
        String str;
        while((str = reader.readLine()) != null){
            lines.add(str);
        }
        return new Database(lines);
    }

    public Database(List<String> lines){
        infoList = new ArrayList<>();
        String[] fields = lines.get(0).split(",");
        this.fields = fields;
        lines.remove(0);
        for (String rawInfo : lines){
            infoList.add(new Info(fields, rawInfo));
        }
    }

    public Database(String[] fields, List<Map<String, String>> data){
        this.fields = fields;
        this.infoList = new ArrayList<>();
        data.forEach(o -> infoList.add(new Info(fields, o)));
    }

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        for(String str : fields){
            sb.append(str).append(",");
        }
        sb.append("\n");
        for(Info info : infoList){
            sb.append(info).append("\n");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        Database d = Database.loadFile("src/data/owid-covid-data.csv");
        System.out.println(d);
    }

    public String[] getFields(){
        return this.fields;
    }

    public List<Map<String, String>> getAllData(){
        List<Map<String, String>> result = new ArrayList<>();
        this.infoList.forEach(o -> result.add(o.getDataDict()));
        return result;
    }

    public void sort(String keywords, double nullVal, boolean isAsc){
        infoList.sort((o1, o2)->{
            try {
                String v1 = o1.getValue(keywords), v2 = o2.getValue(keywords);
                if (v1 == null){
                    return (int) nullVal;
                }
                if (v2 == null){
                    return (int) -nullVal;
                }
                return v1.compareTo(v2);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return 0;
        });
        if(!isAsc){
            Collections.reverse(infoList);
        }
    }

    public List<Map<String, String>> search(String keyword){
        List<Map<String, String>> result = new ArrayList<>();
        this.infoList.stream().filter(o -> o.toString().contains(keyword)).forEach(o -> result.add(o.getDataDict()));
        return result;
    }


}
