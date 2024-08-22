package com.test.tools.model;

import com.alibaba.excel.EasyExcel;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

public class ExlTest {
    public static void main(String[] args) {
        List<Map<Integer,String>> objects = EasyExcel.read("/Users/lishagrace/IdeaProjects/mywork/test2.xlsx").
                sheet().doReadSync();
        //标题
        Map<Integer, String> title = objects.stream().findFirst().get();
        //内容
        Map<String, List<Map<Integer, String>>> content = objects.subList(1,objects.size()).stream().collect(Collectors.groupingBy(r -> r.get(Integer.parseInt("2"))));

        List<String> parts = content.entrySet().stream().map(r -> r.getKey()).collect(Collectors.toList());
        //穷举
        List<List<String>> combinations = getCombinations(parts.stream().toArray(String[]::new));

        // 读取经纬度
        List<Map<Integer,String>> xyList = EasyExcel.read("/Users/lishagrace/IdeaProjects/mywork/xy.xlsx").
                sheet().doReadSync();
        Map<String, List<Map<Integer, String>>> xyMap = xyList.stream().collect(Collectors.groupingBy(r -> r.get(Integer.parseInt("1"))));


        Map<String, List<Map<Integer, Object>>> result = new LinkedHashMap<>();
        for (List<String> combination : combinations) {
            if(!CollectionUtils.isEmpty(combination)) {
                //数据组装
                List<Map<Integer,String>> total = new ArrayList<>();
                for(String part:combination){
                    total.addAll(content.get(part));
                }
                List<Map<Integer, Object>> totalList = mergeIndexDuplicate(total,xyMap);
                result.put(combination.stream().collect(Collectors.joining(",")),totalList);
            }
        }

        String fileName = "/Users/lishagrace/IdeaProjects/mywork/" + "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        List<Map<Integer,Object>> outList = new ArrayList<>();
        Map<Integer, Object> newTitle = new HashMap<>();
        newTitle.put(0,"区域");
        newTitle.put(1,"经/纬度");
        List<String> indexList = new ArrayList<>();
        indexList.add("1985-1994");
        indexList.add("1995-2004");
        indexList.add("2005-2014");
        indexList.add("2015-2023");
        for(int i=0;i< indexList.size();i++){
            newTitle.put(i+2,indexList.get(i));
        }
        // outList.add(newTitle);
        for(Map.Entry<String,List<Map<Integer, Object>> > entry : result.entrySet()){
            outList.addAll(getListVyIndex(entry.getKey(),entry.getValue(), indexList));
        }
        String minFileName = "/Users/lishagrace/IdeaProjects/mywork/" + "差额" + System.currentTimeMillis() + ".xlsx";
        EasyExcel.write(minFileName).sheet("差额").doWrite(outList);
        Map<Object, List<Map<Integer, Object>>> outMap = outList.stream().collect(Collectors.groupingBy(r -> r.get(0),LinkedHashMap::new,Collectors.toList()));

        //1）生成所有的联盟
        int n = parts.size();
        long parse = getParse(n);
        List<Map<Integer, Object>> listVyIndex = new ArrayList<>();
        listVyIndex.add(newTitle);
        //2）计算成员的边际贡献
        for (String part:parts){
            Map<Integer,Object> outResultX = new LinkedHashMap<>();
            Map<Integer,Object> outResultY = new LinkedHashMap<>();
            outResultX.put(0,part);
            outResultY.put(0,part);

            // 包含该大洲的数据
            List<Map.Entry<Object, List<Map<Integer, Object>>>> collect = outMap.entrySet().stream().filter(r -> r.getKey().toString().contains(part)).collect(Collectors.toList());
            List<Map.Entry<Object, List<Map<Integer, Object>>>> no_collect = outMap.entrySet().stream().filter(r -> !r.getKey().toString().contains(part)).collect(Collectors.toList());
            for(int k = 0;k<indexList.size() ;k++){
                // 计算差额
                BigDecimal total = BigDecimal.ZERO;
                BigDecimal k_payoff_y = BigDecimal.ZERO;
                BigDecimal k_payoff_x = BigDecimal.ZERO;
                int i = 0;
                int index = 3+k;
                for(Map.Entry<Object, List<Map<Integer, Object>>> set :collect){
                    String key = set.getKey().toString();
                    int s = key.split(",").length;
                    BigDecimal k_weight = BigDecimal.valueOf(getParse(s-1)).multiply(BigDecimal.valueOf(getParse(n-s))).divide(BigDecimal.valueOf(parse),6,RoundingMode.HALF_UP);
                    total = total.add(k_weight);
                    //  计算差额
                    List<Map<Integer, Object>> value = set.getValue();
                    if(s == 1){
                        k_payoff_y =  convertToBigDecimal(value.get(0).get(index)).multiply(k_weight).add(k_payoff_y);
                        k_payoff_x =  convertToBigDecimal(value.get(1).get(index)).multiply(k_weight).add(k_payoff_x);
                    }else{
                        Map.Entry<Object, List<Map<Integer, Object>>> no_k_coalition = no_collect.get(i - 1);
                        System.out.println(value.get(0).get(0));
                        System.out.println(no_k_coalition.getValue().get(0).get(0));
                        for(int j=0;j<value.size();j++){
                            if(j==0){
                                outResultY.put(1,value.get(j).get(1));
                                BigDecimal y1 =  convertToBigDecimal(value.get(j).get(index));
                                BigDecimal y2 = convertToBigDecimal(no_k_coalition.getValue().get(0).get(index));
                                k_payoff_y = y1.subtract(y2).multiply(k_weight).add(k_payoff_y);
                            }
                            if(j==1){
                                outResultX.put(1,value.get(j).get(1));
                                BigDecimal x1 =  convertToBigDecimal(value.get(j).get(index));
                                BigDecimal x2 = convertToBigDecimal(no_k_coalition.getValue().get(j).get(index));
                                k_payoff_x = x1.subtract(x2).multiply(k_weight).add(k_payoff_x);
                            }
                        }
                    }
                    i++;
                }
                outResultX.put(2+k,k_payoff_x);
                outResultY.put(2+k,k_payoff_y);
            }


            listVyIndex.add(outResultY);
            listVyIndex.add(outResultX);
        }

        //3）计算联盟的权重系数


        //EasyExcel.write(fileName).sheet("模板").doWrite(outList);
        EasyExcel.write(fileName).sheet("模板").doWrite(listVyIndex);
    }

    private static long getParse(long number){
        long factorial = LongStream.rangeClosed(1, number)
                .reduce(1, (accumulator, item) -> accumulator * item);
        return factorial;
    }

    private static List<Map<Integer, Object>>  getListVyIndex(String key,List<Map<Integer, Object>> totalList,List<String> indexList){
        List<Map<Integer, Object>> list = new ArrayList<>();
        for(Map<Integer, Object> map : totalList){
            Map<Integer,Object> sysResult = new LinkedHashMap<>();
            sysResult.put(0,key);
            if(map.containsKey(Integer.parseInt("1"))){
                sysResult.put(Integer.parseInt("1"),map.get(Integer.parseInt("1")));
            }
            sysResult.put(2,null);
            for(int i = 0;i<indexList.size();i++){
                BigDecimal start = BigDecimal.ZERO;
                BigDecimal end = BigDecimal.ZERO;
                String[] split = indexList.get(i).split("-");
                Integer startIndex = Integer.parseInt(split[0])-1900+3;
                Integer endIndex = Integer.parseInt(split[1])-1900+3;
                if(map.containsKey(startIndex)){
                    start = convertToBigDecimal(map.get(startIndex));
                }

                if(map.containsKey(endIndex)){
                    end  = convertToBigDecimal(map.get(endIndex));
                }
                sysResult.put(i+3,convertToBigDecimal(end.subtract(start)));
            }

            list.add(sysResult);

        }
        return list;
    }

    public static List<Map<Integer, Object>> mergeIndexDuplicate(List<Map<Integer, String>> list, Map<String, List<Map<Integer, String>>> xyMap ) {
        //分子
        Map<Integer, Object> fx = new HashMap<>();
        Map<Integer, Object> fy = new HashMap<>();
        //分母
        Map<Integer, Object> mergedMap = new HashMap<>();
        List<Map<Integer, Object>> mergedList = new ArrayList<>();
        for (Map<Integer, String> map : list) {
            String country = map.get(Integer.parseInt("1"));
            BigDecimal x= convertToBigDecimal(getVal(xyMap.get(country),(Integer.parseInt("4"))));
            BigDecimal y= convertToBigDecimal(getVal(xyMap.get(country),(Integer.parseInt("3"))));
            for (Map.Entry<Integer, String> entry : map.entrySet()) {
                if(entry.getKey()<= 2){
                    continue;
                }
                if(entry.getKey()>129){
                    break;
                }

                if (mergedMap.containsKey(entry.getKey())) {
                    // 如果已存在该索引，则累加
                    Object currentValue = mergedMap.get(entry.getKey());
                    mergedMap.put(entry.getKey(), convertToBigDecimal(currentValue).add(convertToBigDecimal(entry.getValue())));
                } else {
                    // 如果不存在该索引，直接添加
                    mergedMap.put(entry.getKey(), convertToBigDecimal(entry.getValue()));
                }
                // x
                if (fx.containsKey(entry.getKey())) {
                    Object currentValue = fx.get(entry.getKey());
                    fx.put(entry.getKey(), convertToBigDecimal(currentValue).add(convertToBigDecimal(entry.getValue()).multiply(x)));
                }else{
                    fx.put(entry.getKey(), convertToBigDecimal(entry.getValue()).multiply(x));
                }
                // y
                if (fy.containsKey(entry.getKey())) {
                    Object currentValue = fy.get(entry.getKey());
                    fy.put(entry.getKey(), convertToBigDecimal(currentValue).add(convertToBigDecimal(entry.getValue()).multiply(y)));
                }else{
                    fy.put(entry.getKey(), convertToBigDecimal(entry.getValue()).multiply(y));
                }
            }
        }
        // 每次遍历一个map后，将累加后的数据添加到mergedList中，并重置mergedMap
        // 遍历最后结果

        for (Map.Entry<Integer, Object> entry : fy.entrySet()) {
            if(entry.getKey()<= 2){
                continue;
            }
            if(entry.getKey()>129){
                break;
            }

            if(convertToBigDecimal(mergedMap.get(entry.getKey())).compareTo(BigDecimal.ZERO) == 0){
                fy.put(entry.getKey(),null);
            }else{
                fy.put(entry.getKey(),convertToBigDecimal(entry.getValue()).divide(convertToBigDecimal(mergedMap.get(entry.getKey())),6, RoundingMode.HALF_UP));
            }
        }
        for (Map.Entry<Integer, Object> entry : fx.entrySet()) {


            if(entry.getKey()<= 2){
                continue;
            }
            if(entry.getKey()>129){
                break;
            }

            if(convertToBigDecimal(mergedMap.get(entry.getKey())).compareTo(BigDecimal.ZERO) == 0){
                fx.put(entry.getKey(),null);
            }else{
                fx.put(entry.getKey(),convertToBigDecimal(entry.getValue()).divide(convertToBigDecimal(mergedMap.get(entry.getKey())),6,RoundingMode.HALF_UP));
            }


        }

        fy.put(1,"经度");
        fx.put(1,"纬度");

        mergedList.add(new HashMap<>(fy));
        mergedList.add(new HashMap<>(fx));
        return mergedList;
    }

    private static BigDecimal getVal(List<Map<Integer, String>> list, Integer index){
        Map<Integer, String> result = new LinkedHashMap<>();
        list.forEach(r->{
            result.putAll(r);
        });
      return convertToBigDecimal(result.get(index));

    }

    public static BigDecimal convertToBigDecimal(Object object) {
        try {
            if (object instanceof BigDecimal) {
                return (BigDecimal) object;
            } else if (object instanceof String) {
                return new BigDecimal((String) object);
            } else if (object instanceof Number) {
                return new BigDecimal(((Number) object).doubleValue());
            } else {
                return BigDecimal.ZERO;
            }
        }catch (Exception e){
            return BigDecimal.ZERO;
        }

    }


    public static List<List<String>> getCombinations(String[] array) {
        List<List<String>> result = new ArrayList<>();
        combinationHelper(array, 0, new ArrayList<>(), result);
        return result;
    }

    private static void combinationHelper(String[] array, int start, List<String> combinationSoFar, List<List<String>> result) {
        if (start == array.length) {
            result.add(new ArrayList<>(combinationSoFar));
            return;
        }
        // 不选择当前元素
        combinationHelper(array, start + 1, combinationSoFar, result);
        // 选择当前元素
        combinationSoFar.add(array[start]);
        combinationHelper(array, start + 1, combinationSoFar, result);
        combinationSoFar.remove(combinationSoFar.size() - 1); // 回溯
    }
}
