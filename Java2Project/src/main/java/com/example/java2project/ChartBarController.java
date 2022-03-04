package com.example.java2project;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.*;

import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.util.Callback;

import javax.imageio.ImageIO;

public class ChartBarController {


    DataManipulationImpl im = new DataManipulationImpl();
    static int playSpeed = 0;
    double sliderValue = 0;
    ArrayList<String> sourceArr = new ArrayList<>();
    ArrayList<String> valueSelectArr = new ArrayList<>();
    String keyword = "";
    int limit = 15;

    List<Map<String, String>> data;
    List<List<Map<String, String>>> datas;
    List<List<Map<String, String>>> datas2;

    @FXML
    private DatePicker startDatePicker = new DatePicker();

    @FXML
    private DatePicker endDatePicker = new DatePicker();

    @FXML
    private ChoiceBox sourceSelect;
    @FXML
    private ChoiceBox valueSelect;

    @FXML
    private Label caption;
    @FXML
    private PieChart pieChart;
    @FXML
    private BarChart barChart;
    @FXML
    private LineChart lineChart;
    @FXML
    private BubbleChart bubbleChart;
    @FXML
    private Slider slider;
    @FXML
    private TextField textField;
    @FXML
    private TabPane tabPane;

    @FXML
    private Tab tab1;
    @FXML
    private Tab tab2;
    @FXML
    private Tab tab3;
    @FXML
    private Tab tab4;
    @FXML
    private Tab tab5;

    @FXML
    private TableView tableView;



    String findName(String s) {
        if (s.equals("country")) {
            if (im.DataSource().equals("who")) return valueSelectArr.get(2);
            else return "location";
        }
        if (s.equals("date")) {
            if (im.DataSource().equals("who")) return valueSelectArr.get(0);
            else return "date";
        }
        if (s.equals("bubble1")) {
            if (im.DataSource().equals("who")) return valueSelectArr.get(4);
            else return valueSelectArr.get(5);
        }
        if (s.equals("bubble2")) {
            if (im.DataSource().equals("who")) return valueSelectArr.get(6);
            else return valueSelectArr.get(8);
        }
        if (s.equals("bubble3")) {
            if (im.DataSource().equals("who")) return valueSelectArr.get(5);
            else return valueSelectArr.get(4);
        }
        if (s.equals("bubble4")) {
            if (im.DataSource().equals("who")) return valueSelectArr.get(7);
            else return valueSelectArr.get(7);
        }
        return "";
    }

    @FXML
    private void LineChartSelect() {
        //System.out.println("?");
    }

    @FXML
    void SaveResultDataButton() {
        try {
            String s = LocalDateTime.now().toString().substring(0, 19).replace(":", "_");
            im.saveResultData(data, "dataSave/" + s + ".csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void ScreenShot() {
        ButtonPause();
        try {

            Robot robot = new Robot();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle screenRectangle = new Rectangle(screenSize);
            Rectangle rec = new Rectangle((int) (0), (int) (0), (int) (600), (int) (500));
            BufferedImage buffimg = robot.createScreenCapture(screenRectangle);

            String s = LocalDateTime.now().toString().substring(0, 19).replace(":", "_");

            ImageIO.write(buffimg, "png", new File(System.getProperty("user.dir") + "/screenshot/" + s + ".png"));

        } catch (AWTException | IOException e) {
            e.printStackTrace();
        }


    }

    @FXML
    private void SearchButton() {

        data = im.dateLimit(String.valueOf(startDatePicker.getValue()), String.valueOf(endDatePicker.getValue())
                , im.getSearchResult(textField.getText().trim()));
        TableRefresh();
    }

    @FXML
    private void initialize() {

        String s[] = im.getDataSourceList();


        sourceArr.addAll(Arrays.asList(s));
        SelectInitialize(sourceSelect, sourceArr);

        sourceSelect.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> ov, Number oldNum, Number newNum) -> {
                    try {
                        im.chooseDataSource(sourceArr.get((int) newNum));
                        data = im.getAllTableData();
                        valueSelectArr = new ArrayList<>();
                        valueSelectArr.addAll(Arrays.asList(im.getTableHead()));
                        SelectInitialize(valueSelect, valueSelectArr);

                        tableView.getColumns().clear();
                        for (int i = 0; i < valueSelectArr.size(); i++) {
                            TableColumn lastNameCol = new TableColumn(valueSelectArr.get(i));
                            tableView.getColumns().addAll(lastNameCol);
                            lastNameCol.setCellValueFactory(
                                    new PropertyValueFactory<>("v" + (i + 1)));
                            lastNameCol.setCellFactory(TextFieldTableCell.<Country>forTableColumn());
                        }

                        TableRefresh();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        sourceSelect.getSelectionModel().selectLast();

        valueSelect.getSelectionModel().selectedIndexProperty().addListener(
                (ObservableValue<? extends Number> ov, Number oldNum, Number newNum) -> {
                    if ((int) newNum == -1) return;
                    keyword = valueSelectArr.get((int) newNum);

                    try {
                        data = im.getSortResult(keyword);
                        Collections.reverse(data);
                        SliderResize();
                        TableRefresh();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    SliderResize();
                }
        );


        final Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        //im.setDate(startDatePicker.getValue(),endDatePicker.getValue());


                        long p = ChronoUnit.DAYS.between(startDatePicker.getValue(), item);
                        setTooltip(new Tooltip("You are about to check for " + p + " days data in total"));
                    }
                };
            }
        };
        endDatePicker.setDayCellFactory(dayCellFactory);
        endDatePicker.setValue(LocalDate.now());
        startDatePicker.setValue(endDatePicker.getValue().minusYears(2));

//        final NumberAxis yAxis = new NumberAxis();
//        final CategoryAxis xAxis = new CategoryAxis();
//        yAxis.setLabel("Value");
//
//        xAxis.setLabel("Item");
//        xAxis.setTickLabelRotation(90);

        pieChart.setAnimated(false);
        barChart.setAnimated(false);
        bubbleChart.setAnimated(false);

        caption.setTextFill(Color.GREEN);
        caption.setStyle("-fx-font: 24 arial;");


        slider.valueProperty().addListener((ov, old_val, new_val) -> setSliderValue((int) slider.getValue()));


        DisplayRunner displayRunner = new DisplayRunner();
        Thread thread = new Thread(displayRunner);
        thread.start();


    }


    private void SelectInitialize(ChoiceBox cb, ArrayList<String> arrayList) {
        cb.setItems(FXCollections.observableArrayList(arrayList));
    }

    void TableRefresh() {

        ObservableList item = FXCollections.observableArrayList();
        for (Map map : data
        ) {
            if (im.DataSource().equals("who"))
                item.add(new Country((String) map.get(valueSelectArr.get(0)),
                        (String) map.get(valueSelectArr.get(1)),
                        (String) map.get(valueSelectArr.get(2)),
                        (String) map.get(valueSelectArr.get(3)),
                        (String) map.get(valueSelectArr.get(4)),
                        (String) map.get(valueSelectArr.get(5)),
                        (String) map.get(valueSelectArr.get(6)),
                        (String) map.get(valueSelectArr.get(7))));
            else
                item.add(new Country((String) map.get(valueSelectArr.get(0)),
                        (String) map.get(valueSelectArr.get(1)),
                        (String) map.get(valueSelectArr.get(2)),
                        (String) map.get(valueSelectArr.get(3)),
                        (String) map.get(valueSelectArr.get(4)),
                        (String) map.get(valueSelectArr.get(5)),
                        (String) map.get(valueSelectArr.get(6)),
                        (String) map.get(valueSelectArr.get(7)),
                        (String) map.get(valueSelectArr.get(8)),
                        (String) map.get(valueSelectArr.get(9)),
                        (String) map.get(valueSelectArr.get(10)),
                        (String) map.get(valueSelectArr.get(11)),
                        (String) map.get(valueSelectArr.get(12)),
                        (String) map.get(valueSelectArr.get(13)),
                        (String) map.get(valueSelectArr.get(14)),
                        (String) map.get(valueSelectArr.get(15)),
                        (String) map.get(valueSelectArr.get(16)),
                        (String) map.get(valueSelectArr.get(17)),
                        (String) map.get(valueSelectArr.get(18)),
                        (String) map.get(valueSelectArr.get(19)),
                        (String) map.get(valueSelectArr.get(20)),
                        (String) map.get(valueSelectArr.get(21)),
                        (String) map.get(valueSelectArr.get(22)),
                        (String) map.get(valueSelectArr.get(23)),
                        (String) map.get(valueSelectArr.get(24)),
                        (String) map.get(valueSelectArr.get(25)),
                        (String) map.get(valueSelectArr.get(26)),
                        (String) map.get(valueSelectArr.get(27)),
                        (String) map.get(valueSelectArr.get(28)),
                        (String) map.get(valueSelectArr.get(29)),
                        (String) map.get(valueSelectArr.get(30)),
                        (String) map.get(valueSelectArr.get(31)),
                        (String) map.get(valueSelectArr.get(32)),
                        (String) map.get(valueSelectArr.get(33)),
                        (String) map.get(valueSelectArr.get(34)),
                        (String) map.get(valueSelectArr.get(35)),
                        (String) map.get(valueSelectArr.get(36)),
                        (String) map.get(valueSelectArr.get(17)),
                        (String) map.get(valueSelectArr.get(38)),
                        (String) map.get(valueSelectArr.get(39)),
                        (String) map.get(valueSelectArr.get(40)),
                        (String) map.get(valueSelectArr.get(41)),
                        (String) map.get(valueSelectArr.get(42)),
                        (String) map.get(valueSelectArr.get(43)),
                        (String) map.get(valueSelectArr.get(44)),
                        (String) map.get(valueSelectArr.get(45)),
                        (String) map.get(valueSelectArr.get(46)),
                        (String) map.get(valueSelectArr.get(47)),
                        (String) map.get(valueSelectArr.get(48)),
                        (String) map.get(valueSelectArr.get(49)),
                        (String) map.get(valueSelectArr.get(50)),
                        (String) map.get(valueSelectArr.get(51)),
                        (String) map.get(valueSelectArr.get(52)),
                        (String) map.get(valueSelectArr.get(53)),
                        (String) map.get(valueSelectArr.get(54)),
                        (String) map.get(valueSelectArr.get(55)),
                        (String) map.get(valueSelectArr.get(56)),
                        (String) map.get(valueSelectArr.get(57)),
                        (String) map.get(valueSelectArr.get(58)),
                        (String) map.get(valueSelectArr.get(59)),
                        (String) map.get(valueSelectArr.get(60)),
                        (String) map.get(valueSelectArr.get(61)),
                        (String) map.get(valueSelectArr.get(62)),
                        (String) map.get(valueSelectArr.get(63)),
                        (String) map.get(valueSelectArr.get(64)),
                        (String) map.get(valueSelectArr.get(65)),
                        (String) map.get(valueSelectArr.get(66))
                ));
        }
        tableView.setItems(item);

    }

    void SliderResize() {
        slider.setValue(0);

        slider.setMin(0);

        datas = im.groupByFunction(findName("date"), keyword);
        datas = datas.stream().filter(e -> e.get(0).get(findName("date")).compareTo(startDatePicker.getValue().toString()) >= 0
                && e.get(0).get(findName("date")).compareTo(endDatePicker.getValue().toString()) <= 0).collect(Collectors.toList());
        datas2 = im.groupByFunction(findName("country"), findName("date"));

        slider.setMax(datas.size() - 1);
    }

    void ChartRefresh(int index) {


        List<Map<String, String>> dataNow = datas.get(index);




        if (tab2.isSelected()) {

            ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
            for (int i = 1; i <= Math.min(limit,dataNow.size()); i++) {
                pieChartData.add(new PieChart.Data(dataNow.get(dataNow.size() - i).get(findName("country")), Double.parseDouble(dataNow.get(dataNow.size() - i).get(keyword))));
            }
            pieChart.titleProperty().set(dataNow.get(0).get(findName("date")));
            pieChart.setData(pieChartData);
            for (PieChart.Data data : pieChart.getData()) {

                data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                        e -> {
                            caption.setVisible(true);
                            caption.setTranslateX(e.getSceneX() + 20);
                            caption.setTranslateY(e.getSceneY() + 20);
                        });
                data.getNode().addEventHandler(MouseEvent.MOUSE_MOVED,
                        e -> {
                            caption.setVisible(true);
                            caption.setTranslateX(e.getSceneX() + 20);
                            caption.setTranslateY(e.getSceneY() + 20);
                            caption.setText(data.getName() + " : " + (data.getPieValue()));
                        });
                data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
                        e -> caption.setVisible(false)
                );
            }
        }

        if (tab3.isSelected()) {


            try {
                barChart.titleProperty().set(dataNow.get(0).get(findName("date")));

                XYChart.Series series1 = new XYChart.Series();
                series1.setName(dataNow.get(0).get(findName("date")));
                for (int i = 1; i <= Math.min(limit, dataNow.size()); i++) {
                    series1.getData().add(new XYChart.Data(Double.parseDouble(dataNow.get(dataNow.size() - i).get(keyword)),
                            dataNow.get(dataNow.size() - i).get(findName("country"))));
                }

                barChart.getData().clear();
                barChart.getData().addAll(series1);
            }
            catch (Exception e){
                System.out.println("");
            }

//
//        series1.getData().sort(new Comparator() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                return (int) ((XYChart.Data) o1).getXValue() - (int) ((XYChart.Data) o2).getXValue();
//            }
//        });



        }

        if (tab4.isSelected()) {
            List<Map<String, String>> dataNow2 = datas2.get(Math.min(index, datas2.size() - 1));
            XYChart.Series series3 = new XYChart.Series();
            series3.setName(dataNow2.get(0).get(findName("country")));
            for (int i = 0; i < dataNow2.size(); i++) {
                if (dataNow2.get(i).get(findName("date")).compareTo(startDatePicker.getValue().toString()) >= 0 &&
                        dataNow2.get(i).get(findName("date")).compareTo(endDatePicker.getValue().toString()) <= 0)
                    series3.getData().add(new XYChart.Data(dataNow2.get(i).get(findName("date")),  Double.parseDouble(dataNow2.get(i).get(keyword))));
            }
            lineChart.getData().clear();
            lineChart.getData().add(series3);
        }

        if (tab5.isSelected()) {


            bubbleChart.titleProperty().set(dataNow.get(0).get(findName("date")));
            bubbleChart.getData().clear();

            for (int i = 1; i < Math.min(limit,dataNow.size()); i++) {
                XYChart.Series series2 = new XYChart.Series();
                series2.setName(dataNow.get(i).get(findName("country")));
                series2.getData().add(new XYChart.Data(Double.parseDouble(dataNow.get(dataNow.size() - i).get(findName("bubble1")))/10, Double.parseDouble(dataNow.get(dataNow.size() - i).get(findName("bubble2"))),
                        Double.parseDouble(dataNow.get(dataNow.size() - i).get(findName("bubble3")))/Double.parseDouble(dataNow.get(dataNow.size() - i).get(findName("bubble4")))));
                bubbleChart.getData().addAll(series2);
            }

        }
    }

    void test() {


        for (PieChart.Data data : pieChart.getData()) {

            data.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED,
                    e -> {
                        caption.setVisible(true);
                        caption.setTranslateX(e.getSceneX() + 20);
                        caption.setTranslateY(e.getSceneY() + 20);
                    });
            data.getNode().addEventHandler(MouseEvent.MOUSE_MOVED,
                    e -> {
                        caption.setVisible(true);
                        caption.setTranslateX(e.getSceneX() + 20);
                        caption.setTranslateY(e.getSceneY() + 20);
                        caption.setText(data.getName() + " : " + (data.getPieValue()));
                    });
            data.getNode().addEventHandler(MouseEvent.MOUSE_EXITED,
                    e -> caption.setVisible(false)
            );
        }
    }

    void setSliderValue(int x) {
        if ((int) slider.getValue() == sliderValue) return;
        slider.setValue(x);
        ChartRefresh(x);
    }


    @FXML
    void updateSliderValue() {
        setSliderValue((int) sliderValue);
    }

    @FXML
    protected void ButtonStop() {
        sliderValue = 0;
        setSliderValue(0);
        playSpeed = 0;
    }

    @FXML
    protected void ButtonPlay() {
        sliderValue = slider.getValue();
        playSpeed = 1;
    }

    @FXML
    protected void ButtonPause() {
        sliderValue = slider.getValue();
        playSpeed = 0;
    }

    @FXML
    protected void ButtonPlayFast() {
        sliderValue = slider.getValue();
        playSpeed = 2;
    }

    @FXML
    protected void ButtonReverse() {
        sliderValue = slider.getValue();
        playSpeed = -3;
    }

    @FXML
    protected void LeftClick() {
        sliderValue = slider.getValue();
        sliderValue = sliderValue - 1;
        updateSliderValue();
    }

    @FXML
    protected void RightClick() {
        sliderValue = slider.getValue();
        sliderValue = sliderValue + 1;
        updateSliderValue();
    }


    class DisplayRunner implements Runnable {

        @Override
        public void run() {
            while (true) {
                try {
                    Thread.sleep(150);
                    Platform.runLater(() -> {
                        sliderValue = sliderValue + playSpeed * 0.3;
                        if (playSpeed != 0)
                            updateSliderValue();
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    protected void UpdateData() {
        try {
            im.updataDateByCrawler();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Country {

        private final SimpleStringProperty v1;
        private final SimpleStringProperty v2;
        private final SimpleStringProperty v3;
        private final SimpleStringProperty v4;
        private final SimpleStringProperty v5;
        private final SimpleStringProperty v6;
        private final SimpleStringProperty v7;
        private final SimpleStringProperty v8;
        private final SimpleStringProperty v9;
        private final SimpleStringProperty v10;
        private final SimpleStringProperty v11;
        private final SimpleStringProperty v12;
        private final SimpleStringProperty v13;
        private final SimpleStringProperty v14;
        private final SimpleStringProperty v15;
        private final SimpleStringProperty v16;
        private final SimpleStringProperty v17;
        private final SimpleStringProperty v18;
        private final SimpleStringProperty v19;
        private final SimpleStringProperty v20;
        private final SimpleStringProperty v21;
        private final SimpleStringProperty v22;
        private final SimpleStringProperty v23;
        private final SimpleStringProperty v24;
        private final SimpleStringProperty v25;
        private final SimpleStringProperty v26;
        private final SimpleStringProperty v27;
        private final SimpleStringProperty v28;
        private final SimpleStringProperty v29;
        private final SimpleStringProperty v30;
        private final SimpleStringProperty v31;
        private final SimpleStringProperty v32;
        private final SimpleStringProperty v33;
        private final SimpleStringProperty v34;
        private final SimpleStringProperty v35;
        private final SimpleStringProperty v36;
        private final SimpleStringProperty v37;
        private final SimpleStringProperty v38;
        private final SimpleStringProperty v39;
        private final SimpleStringProperty v40;
        private final SimpleStringProperty v41;
        private final SimpleStringProperty v42;
        private final SimpleStringProperty v43;
        private final SimpleStringProperty v44;
        private final SimpleStringProperty v45;
        private final SimpleStringProperty v46;
        private final SimpleStringProperty v47;
        private final SimpleStringProperty v48;
        private final SimpleStringProperty v49;
        private final SimpleStringProperty v50;
        private final SimpleStringProperty v51;
        private final SimpleStringProperty v52;
        private final SimpleStringProperty v53;
        private final SimpleStringProperty v54;
        private final SimpleStringProperty v55;
        private final SimpleStringProperty v56;
        private final SimpleStringProperty v57;
        private final SimpleStringProperty v58;
        private final SimpleStringProperty v59;
        private final SimpleStringProperty v60;
        private final SimpleStringProperty v61;
        private final SimpleStringProperty v62;
        private final SimpleStringProperty v63;
        private final SimpleStringProperty v64;
        private final SimpleStringProperty v65;
        private final SimpleStringProperty v66;
        private final SimpleStringProperty v67;
        private final SimpleStringProperty v68;
        private final SimpleStringProperty v69;

        private Country(String v1, String v2, String v3, String v4, String v5, String v6, String v7, String v8) {
            this.v1 = new SimpleStringProperty(v1);
            this.v2 = new SimpleStringProperty(v2);
            this.v3 = new SimpleStringProperty(v3);
            this.v4 = new SimpleStringProperty(v4);
            this.v5 = new SimpleStringProperty(v5);
            this.v6 = new SimpleStringProperty(v6);
            this.v7 = new SimpleStringProperty(v7);
            this.v8 = new SimpleStringProperty(v8);
            this.v9 = null;
            this.v10 = null;
            this.v11 = null;
            this.v12 = null;
            this.v13 = null;
            this.v14 = null;
            this.v15 = null;
            this.v16 = null;
            this.v17 = null;
            this.v18 = null;
            this.v19 = null;
            this.v20 = null;
            this.v21 = null;
            this.v22 = null;
            this.v23 = null;
            this.v24 = null;
            this.v25 = null;
            this.v26 = null;
            this.v27 = null;
            this.v28 = null;
            this.v29 = null;
            this.v30 = null;
            this.v31 = null;
            this.v32 = null;
            this.v33 = null;
            this.v34 = null;
            this.v35 = null;
            this.v36 = null;
            this.v37 = null;
            this.v38 = null;
            this.v39 = null;
            this.v40 = null;
            this.v41 = null;
            this.v42 = null;
            this.v43 = null;
            this.v44 = null;
            this.v45 = null;
            this.v46 = null;
            this.v47 = null;
            this.v48 = null;
            this.v49 = null;
            this.v50 = null;
            this.v51 = null;
            this.v52 = null;
            this.v53 = null;
            this.v54 = null;
            this.v55 = null;
            this.v56 = null;
            this.v57 = null;
            this.v58 = null;
            this.v59 = null;
            this.v60 = null;
            this.v61 = null;
            this.v62 = null;
            this.v63 = null;
            this.v64 = null;
            this.v65 = null;
            this.v66 = null;
            this.v67 = null;
            this.v68 = null;
            this.v69 = null;
        }

        private Country(String v1, String v2, String v3, String v4, String v5, String v6, String v7, String v8, String v9, String v10,
                        String v11, String v12, String v13, String v14, String v15, String v16, String v17, String v18, String v19,
                        String v20, String v21, String v22, String v23, String v24, String v25, String v26, String v27, String v28, String v29,
                        String v30, String v31, String v32, String v33, String v34, String v35, String v36, String v37, String v38, String v39,
                        String v40, String v41, String v42, String v43, String v44, String v45, String v46, String v47, String v48, String v49,
                        String v50, String v51, String v52, String v53, String v54, String v55, String v56, String v57, String v58, String v59,
                        String v60, String v61, String v62, String v63, String v64, String v65, String v66, String v67
        ) {
            this.v1 = new SimpleStringProperty(v1);
            this.v2 = new SimpleStringProperty(v2);
            this.v3 = new SimpleStringProperty(v3);
            this.v4 = new SimpleStringProperty(v4);
            this.v5 = new SimpleStringProperty(v5);
            this.v6 = new SimpleStringProperty(v6);
            this.v7 = new SimpleStringProperty(v7);
            this.v8 = new SimpleStringProperty(v8);
            this.v9 = new SimpleStringProperty(v9);
            this.v10 = new SimpleStringProperty(v10);
            this.v11 = new SimpleStringProperty(v11);
            this.v12 = new SimpleStringProperty(v12);
            this.v13 = new SimpleStringProperty(v13);
            this.v14 = new SimpleStringProperty(v14);
            this.v15 = new SimpleStringProperty(v15);
            this.v16 = new SimpleStringProperty(v16);
            this.v17 = new SimpleStringProperty(v17);
            this.v18 = new SimpleStringProperty(v18);
            this.v19 = new SimpleStringProperty(v19);
            this.v20 = new SimpleStringProperty(v20);
            this.v21 = new SimpleStringProperty(v21);
            this.v22 = new SimpleStringProperty(v22);
            this.v23 = new SimpleStringProperty(v23);
            this.v24 = new SimpleStringProperty(v24);
            this.v25 = new SimpleStringProperty(v25);
            this.v26 = new SimpleStringProperty(v26);
            this.v27 = new SimpleStringProperty(v27);
            this.v28 = new SimpleStringProperty(v28);
            this.v29 = new SimpleStringProperty(v29);
            this.v30 = new SimpleStringProperty(v30);
            this.v31 = new SimpleStringProperty(v31);
            this.v32 = new SimpleStringProperty(v32);
            this.v33 = new SimpleStringProperty(v33);
            this.v34 = new SimpleStringProperty(v34);
            this.v35 = new SimpleStringProperty(v35);
            this.v36 = new SimpleStringProperty(v36);
            this.v37 = new SimpleStringProperty(v37);
            this.v38 = new SimpleStringProperty(v38);
            this.v39 = new SimpleStringProperty(v39);
            this.v40 = new SimpleStringProperty(v40);
            this.v41 = new SimpleStringProperty(v41);
            this.v42 = new SimpleStringProperty(v42);
            this.v43 = new SimpleStringProperty(v43);
            this.v44 = new SimpleStringProperty(v44);
            this.v45 = new SimpleStringProperty(v45);
            this.v46 = new SimpleStringProperty(v46);
            this.v47 = new SimpleStringProperty(v47);
            this.v48 = new SimpleStringProperty(v48);
            this.v49 = new SimpleStringProperty(v49);
            this.v50 = new SimpleStringProperty(v50);
            this.v51 = new SimpleStringProperty(v51);
            this.v52 = new SimpleStringProperty(v52);
            this.v53 = new SimpleStringProperty(v53);
            this.v54 = new SimpleStringProperty(v54);
            this.v55 = new SimpleStringProperty(v55);
            this.v56 = new SimpleStringProperty(v56);
            this.v57 = new SimpleStringProperty(v57);
            this.v58 = new SimpleStringProperty(v58);
            this.v59 = new SimpleStringProperty(v59);
            this.v60 = new SimpleStringProperty(v60);
            this.v61 = new SimpleStringProperty(v61);
            this.v62 = new SimpleStringProperty(v62);
            this.v63 = new SimpleStringProperty(v63);
            this.v64 = new SimpleStringProperty(v64);
            this.v65 = new SimpleStringProperty(v65);
            this.v66 = new SimpleStringProperty(v66);
            this.v67 = new SimpleStringProperty(v67);
            this.v68 = new SimpleStringProperty(v1);
            this.v69 = new SimpleStringProperty(v1);
        }

        public String getV1() {
            return v1.get();
        }

        public String getV2() {
            return v2.get();
        }

        public String getV3() {
            return v3.get();
        }

        public String getV4() {
            return v4.get();
        }

        public String getV5() {
            return v5.get();
        }

        public String getV6() {
            return v6.get();
        }

        public String getV7() {
            return v7.get();
        }

        public String getV8() {
            return v8.get();
        }

        public String getV9() {
            return v9.get();
        }

        public String getV10() {
            return v10.get();
        }

        public String getV11() {
            return v11.get();
        }

        public String getV12() {
            return v12.get();
        }

        public String getV13() {
            return v13.get();
        }

        public String getV14() {
            return v14.get();
        }

        public String getV15() {
            return v15.get();
        }

        public String getV16() {
            return v16.get();
        }

        public String getV17() {
            return v17.get();
        }

        public String getV18() {
            return v18.get();
        }

        public String getV19() {
            return v19.get();
        }

        public String getV20() {
            return v20.get();
        }

        public String getV21() {
            return v21.get();
        }

        public String getV22() {
            return v22.get();
        }

        public String getV23() {
            return v23.get();
        }

        public String getV24() {
            return v24.get();
        }

        public String getV25() {
            return v25.get();
        }

        public String getV26() {
            return v26.get();
        }

        public String getV27() {
            return v27.get();
        }

        public String getV28() {
            return v28.get();
        }

        public String getV29() {
            return v29.get();
        }

        public String getV30() {
            return v30.get();
        }

        public String getV31() {
            return v31.get();
        }

        public String getV32() {
            return v32.get();
        }

        public String getV33() {
            return v33.get();
        }

        public String getV34() {
            return v34.get();
        }

        public String getV35() {
            return v35.get();
        }

        public String getV36() {
            return v36.get();
        }

        public String getV37() {
            return v37.get();
        }

        public String getV38() {
            return v38.get();
        }

        public String getV39() {
            return v39.get();
        }

        public String getV40() {
            return v40.get();
        }

        public String getV41() {
            return v41.get();
        }

        public String getV42() {
            return v42.get();
        }

        public String getV43() {
            return v43.get();
        }

        public String getV44() {
            return v44.get();
        }

        public String getV45() {
            return v45.get();
        }

        public String getV46() {
            return v46.get();
        }

        public String getV47() {
            return v47.get();
        }

        public String getV48() {
            return v48.get();
        }

        public String getV49() {
            return v49.get();
        }

        public String getV50() {
            return v50.get();
        }

        public String getV51() {
            return v51.get();
        }

        public String getV52() {
            return v52.get();
        }

        public String getV53() {
            return v53.get();
        }

        public String getV54() {
            return v54.get();
        }

        public String getV55() {
            return v55.get();
        }

        public String getV56() {
            return v56.get();
        }

        public String getV57() {
            return v57.get();
        }

        public String getV58() {
            return v58.get();
        }

        public String getV59() {
            return v59.get();
        }

        public String getV60() {
            return v60.get();
        }

        public String getV61() {
            return v61.get();
        }

        public String getV62() {
            return v62.get();
        }

        public String getV63() {
            return v63.get();
        }

        public String getV64() {
            return v64.get();
        }

        public String getV65() {
            return v65.get();
        }

        public String getV66() {
            return v66.get();
        }

        public String getV67() {
            return v67.get();
        }

        public String getV68() {
            return v68.get();
        }

        public String getV69() {
            return v69.get();
        }
    }
}


