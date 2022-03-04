package com.example.java2project;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface DataManipulation {

    /**
     *
     * @return 所有可选数据源列表
     */
    public String[] getDataSourceList();

    /**
     *
     * @param dataSource 数据源
     */
    public void chooseDataSource(String dataSource) throws IOException;

    /**
     * 爬取最新数据
     */
    public void updataDateByCrawler() throws Exception;

    /**
     * 获取table的列名(有哪些列)
     * @return 列名数组
     */
    public String[] getTableHead();

    /**
     * 获取table的全部数据
     * @return 一个Map的list，每一个Map表示一行数据；Map: (key: value) -> (列名: 数据)
     */
    public List<Map<String, String>> getAllTableData();

    /**
     * 设置排序时 'null' 的位置
     * @param Order {1， -1} 1 表示 'null' 无限大，-1 表示 'null' 无限小
     */
    public void setNullOrder(int Order);


    /**
     * 获取table排序后结果
     * @param keywords 用户输入的排序关键字
     * @return 排序后数据
     */
    public List<Map<String, String>> getSortResult(String keywords) throws Exception;

    /**
     * 获取table搜索后结果
     * @param sortArguments 用户输入的搜索关键字
     * @return 搜索的数据
     */
    public List<Map<String, String>> getSearchResult(String keywords);

    /**
     * 保存搜索/排序出的数据
     * @param data 数据
     * @param filePath 保存位置
     */
    public void saveResultData(List<Map<String, String>> data, String filePath) throws IOException;


    /**
     * 获取地区(国家)列表
     * @return 地区(国家)列表
     */
    public String[] getLocationsList();

    /**
     * 获取所有数值列的信息 （有的列存储的是字符串，无法绘图）
     * @return 所有数值列列名
     */
    public String[] getNumericalNames();

    /**
     * 返回绘制图表时需要的信息
     * @param location 地区名
     * @param valueName 数据列名
     * @return 一个Map，(key: value) -> (date: value)
     * 这里date使用字符串表示的，也可以是其他数据类型
     */
    public Map<String, String> getSpecificValue(String location, String valueName);

    public List<List<Map<String, String>>> groupByFunction(String groupByKey, String sortByKey);

    public List<Map<String, String>> dateLimit(String start, String end, List<Map<String, String>> data);

}
