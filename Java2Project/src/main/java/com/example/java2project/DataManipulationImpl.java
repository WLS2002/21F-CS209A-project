package com.example.java2project;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.util.*;
import java.util.stream.Collectors;

import com.example.java2project.Crawler.Downloader;
import com.example.java2project.data.Database;

public class DataManipulationImpl implements DataManipulation {

    private final String[] dataSource = {"who", "owid"};
    private Database database;
    private String currentDataSource;
    private double nullValue;

    public String DataSource(){
        return currentDataSource;
    }

    private String getLink(String dataSource) {
        switch (dataSource) {
            case "who":
                return "https://covid19.who.int/WHO-COVID-19-global-data.csv";
            case "owid":
                return "https://github.com/owid/covid-19-data/blob/master/public/data/owid-covid-data.csv?raw=true";
        }
        return null;
    }

    private String getLocationKeyword(String dataSource){
        String keyword = null;
        switch (this.currentDataSource) {
            case "who":
                keyword = "Country";
                break;
            case "woid":
                keyword = "location";
                break;
        }
        return keyword;
    }

    private String getDateKeyword(String dataSource){
        String keyword = null;
        switch (this.currentDataSource) {
            case "who":
                keyword = "\uFEFFDate_reported";
                break;
            case "owid":
                keyword = "date";
                break;
        }
        return keyword;
    }
    @Override
    public String[] getDataSourceList() {
        return dataSource;
    }

    @Override
    public void chooseDataSource(String dataSource) throws IOException {
        this.currentDataSource = dataSource;
        this.database = Database.loadFile(String.format("src/main/java/com/example/java2project/File/%s.csv", this.currentDataSource));
    }

    @Override
    public void updataDateByCrawler() throws Exception {
        Downloader downloader = Downloader.getInstance();
        downloader.download(getLink(this.currentDataSource), String.format("src/main/java/com/example/java2project/File/%s.csv", this.currentDataSource));
    }

    @Override
    public String[] getTableHead() {
        return database.getFields();
    }

    @Override
    public List<Map<String, String>> getAllTableData() {
        return database.getAllData();
    }

    @Override
    public void setNullOrder(int order) {
        if (order > 0) {
            this.nullValue = Double.MAX_VALUE;
        } else if (order < 0) {
            this.nullValue = Double.MIN_VALUE;
        } else {
            this.nullValue = 0;
        }
    }

    @Override
    public List<Map<String, String>> getSortResult(String keywords) throws Exception {
        if (Arrays.stream(database.getFields()).anyMatch(o -> o.equals(keywords))) {
            String[] numericalNames = getNumericalNames();
            List<Map<String, String>> allData = getAllTableData();
            if(Arrays.asList(numericalNames).contains(keywords)) {
                allData.sort(Comparator.comparing(o ->Double.parseDouble(o.get(keywords))));
            }
            else {
                allData.sort(Comparator.comparing(o -> o.get(keywords)));
            }
            return allData;
        }
        throw new Exception("keywords not in filds");
    }

    @Override
    public List<Map<String, String>> getSearchResult(String keywords) {
        return database.search(keywords);
    }

    @Override
    public void saveResultData(List<Map<String, String>> data, String filePath) throws IOException {
        Database tmp = new Database(getTableHead(), data);
        BufferedWriter bf = new BufferedWriter(new FileWriter(filePath));
        bf.write(tmp.toString());
        bf.close();
    }

    @Override
    public String[] getLocationsList() {

        HashSet<String> set = new HashSet<>();
        String finalKeyword = getLocationKeyword(this.currentDataSource);
        database.getAllData().forEach(o -> set.add(o.get(finalKeyword)));
        return set.toArray(new String[]{});
    }

    @Override
    public String[] getNumericalNames() {
        switch (this.currentDataSource) {
            case "owid":
                return new String[]{"total_cases", "new_cases", "new_cases_smoothed", "total_deaths", "new_deaths", "new_deaths_smoothed", "total_cases_per_million", "new_cases_per_million", "new_cases_smoothed_per_million", "total_deaths_per_million", "new_deaths_per_million", "new_deaths_smoothed_per_million", "reproduction_rate", "icu_patients", "icu_patients_per_million", "hosp_patients", "hosp_patients_per_million", "weekly_icu_admissions", "weekly_icu_admissions_per_million", "weekly_hosp_admissions", "weekly_hosp_admissions_per_million", "new_tests", "total_tests", "total_tests_per_thousand", "new_tests_per_thousand", "new_tests_smoothed", "new_tests_smoothed_per_thousand", "positive_rate", "tests_per_case", "total_vaccinations", "people_vaccinated", "people_fully_vaccinated", "total_boosters", "new_vaccinations", "new_vaccinations_smoothed", "total_vaccinations_per_hundred", "people_vaccinated_per_hundred", "people_fully_vaccinated_per_hundred", "total_boosters_per_hundred", "new_vaccinations_smoothed_per_million", "new_people_vaccinated_smoothed", "new_people_vaccinated_smoothed_per_hundred", "stringency_index", "population", "population_density", "median_age", "aged_65_older", "aged_70_older", "gdp_per_capita", "extreme_poverty", "cardiovasc_death_rate", "diabetes_prevalence", "female_smokers", "male_smokers", "handwashing_facilities", "hospital_beds_per_thousand", "life_expectancy", "human_development_index", "excess_mortality_cumulative_absolute", "excess_mortality_cumulative", "excess_mortality", "excess_mortality_cumulative_per_million",};
            case "who":
                return new String[]{"New_cases", "Cumulative_cases", "New_deaths", "Cumulative_deaths"};
        }
        return null;
    }

    @Override
    public Map<String, String> getSpecificValue(String location, String valueName) {
        String locationKeyword = getLocationKeyword(this.currentDataSource);
        String dateKeyword = getDateKeyword(this.currentDataSource);
        HashMap<String, String> result = new HashMap<>();
        this.database.getAllData().stream().filter(o -> o.get(locationKeyword).equals(location)).forEach(o -> result.put(o.get(dateKeyword), o.get(valueName)));
        return result;
    }

    @Override
    public List<List<Map<String, String>>> groupByFunction(String groupByKey, String sortByKey) {
        List<Map<String, String>> data = getAllTableData();
        Map<String, List<Map<String, String>>> result = new TreeMap<>(Comparator.comparing(o -> o));
        data.forEach(m -> {
            if(! result.containsKey(m.get(groupByKey)))
                result.put(m.get(groupByKey), new ArrayList<>());
            result.get(m.get(groupByKey)).add(m);
        });

        String[] numericalNames = getNumericalNames();
        if(Arrays.asList(numericalNames).contains(sortByKey)){
            result.values().forEach(l -> l.sort(Comparator.comparing(o ->Double.parseDouble(o.get(sortByKey)))));
        }
        else
            result.values().forEach(l -> l.sort(Comparator.comparing(o -> o.get(sortByKey))));

        ArrayList<List<Map<String, String>>> list = new ArrayList<>();
        result.keySet().forEach(k -> list.add(result.get(k)));
        return list;
    }

    @Override
    public List<Map<String, String>> dateLimit(String start, String end, List<Map<String, String>> data) {

        String dateKeyword = getDateKeyword(this.currentDataSource);
        System.out.println(dateKeyword);
        return data.stream().filter(d -> start.compareTo(d.get(dateKeyword)) <= 0 && d.get(dateKeyword).compareTo(end) <= 0).collect(Collectors.toList());
    }
}
