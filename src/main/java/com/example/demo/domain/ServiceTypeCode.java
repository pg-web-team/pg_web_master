package com.example.demo.domain;

public enum ServiceTypeCode {

    /**
     * ServiceType enum
     */
    SALES_SERVICE(1, "销售服务"),
    CLASSROOM_TRAINING(2, "课堂培训"),
    INVENTORY(3, "盘点"),
    LUNCH(4, "午餐"),
    DINNER(5, "晚餐"),
    FAMILY_LEAVE(6, "事假"),
    CASE_LEAVE(7, "例休"),
    EMPLOYEE_LEAVE(8, "员工假"),
    SICK_LEAVE(9, "病假"),
    MARRIAGE_LEAVE(10, "婚假"),
    MATERNITY_LEAVE(11, "产假"),
    FAMILY_LEAVE_MATERNITY_LEAVE(12, "事假:待产"),
    ABORTION_LEAVE(13, "流产假"),
    WAITING_FOR_WORK(14, "待岗"),
    FUNERAL_LEAVE(15, "丧假"),
    WORK_INJURY(16, "工伤"),
    ABSENCE_FROM_WORK(17, "旷工"),
    ;

    private int key;

    private String value;

    public int getKey() {
        return key;
    }

    private void setKey(int key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    private void setValue(String value) {
        this.value = value;
    }

    ServiceTypeCode(int key, String value) {
        this.key = key;
        this.value = value;
    }

    public static String getValue(int key) {
        for (ServiceTypeCode ele : values()) {
            if(ele.getKey() == key){
                return ele.getValue();
            }
        }
        return null;
    }

    public static boolean isInclude(Integer key) {
        boolean include = false;

        if (key != null) {
            for (ServiceTypeCode e : ServiceTypeCode.values()) {
                if (e.getKey() == key) {
                    include = true;
                    break;
                }
            }
        }
        return include;
    }


    public static boolean isServiceInclude(Integer key) {
        boolean include = false;

        if (key != null) {
            if (key == SALES_SERVICE.getKey() || key == CLASSROOM_TRAINING.getKey() || key == INVENTORY.getKey()) {
                include = true;
            }
        }
        return include;
    }

    public static boolean isMealInclude(Integer key) {
        boolean include = false;

        if (key != null) {
            if (key == LUNCH.getKey() || key == DINNER.getKey()) {
                include = true;
            }
        }
        return include;
    }

    public static boolean isInStore(Integer key) {
        boolean include = false;

        if (key != null) {
            if (key == SALES_SERVICE.getKey()) {
                include = true;
            }
        }
        return include;
    }

    public static boolean isGoOut(Integer key) {
        boolean include = false;

        if (key != null) {
            if (key == CLASSROOM_TRAINING.getKey() || key == INVENTORY.getKey()) {
                include = true;
            }
        }
        return include;
    }
}

