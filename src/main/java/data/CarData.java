package data;

import bean.CarBean;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Asus- on 2018/7/19.
 */
public class CarData {

    //车品牌
    private static final String[] carBrands = {"迈凯伦", "阿斯顿·马丁", "大众", "丰田", "本田", "奥迪", "宝马", "奔驰", "保时捷", "法拉利"};

    //车型号
    private static final String[][] carModels = {
            {"540C", "570GT", "570S", "600LT", "720S", "P1", "650S", "675LT", "12C", "625C",},
            {"DB11", "DB9", "DBS", "DB6", "Rapide", "Vanquish", "V12 Vantage", "V12 Zagato", "Virage", "Valkyrie"},
            {"朗逸", "迈腾", "宝来", "速腾", "途观L", "T-ROC探歌", "高尔夫", "帕萨特", "辉昂", "捷达"},
            {"卡罗拉", "凯美瑞", "皇冠", "汉兰达", "威驰"},
            {"雅阁", "奥德赛", "本田CR-V", "杰德", "锋范", "缤智"},
            {"A4L", "Q5L", "A3", "A6L", "Q5", "Q7", "A8", "A6", "A7"},
            {"X6", "X4", "730Li", "740Li", "318i", "318Li", "X3", "X5"},
            {"S400", "S500", "A200", "C200", "迈巴赫S560", "迈巴赫S450", "迈巴赫S680"},
            {"Cayenne", "Panamera", "911", "Macan", "718", "918", "Boxster", "Cayman"},
            {"488", "F430", "599", "612", "458", "California T", "GTC4Lusso", "812 Superfast", "F12berlinetta"},
    };

    //省编号
    private static final String[] provinces = {"京", "沪", "粤"};
    //市编号
    private static final String[] cities = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "k", "L", "M", "N",
            "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
    //是否为字母
    private static boolean isChar = false;

    //百家姓
    private static final String[] firstName = {"赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨", "朱", "秦", "尤", "许",
            "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻", "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎",
            "鲁", "韦", "昌", "马", "苗", "凤", "花", "方", "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷",
            "罗", "毕", "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄", "和",
            "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴", "谈", "宋", "茅", "庞", "熊", "纪", "舒",
            "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季", "麻", "强", "贾", "路", "娄", "危", "江", "童", "颜", "郭", "梅", "盛", "林", "刁", "钟",
            "徐", "邱", "骆", "高", "夏", "蔡", "田", "樊", "胡", "凌", "霍", "虞", "万", "支", "柯", "昝", "管", "卢", "莫", "经", "房", "裘", "缪", "干", "解", "应",
            "宗", "丁", "宣", "贲", "邓", "郁", "单", "杭", "洪", "包", "诸", "左", "石", "崔", "吉", "钮", "龚", "程", "嵇", "邢", "滑", "裴", "陆", "荣", "翁", "荀",
            "羊", "于", "惠", "甄", "曲", "家", "封", "芮", "羿", "储", "靳", "汲", "邴", "糜", "松", "井", "段", "富", "巫", "乌", "焦", "巴", "弓", "牧", "隗", "山",
            "谷", "车", "侯", "宓", "蓬", "全", "郗", "班", "仰", "秋", "仲", "伊", "宫", "宁", "仇", "栾", "暴", "甘", "钭", "厉", "戎", "祖", "武", "符", "刘", "景",
            "詹", "束", "龙", "叶", "幸", "司", "韶", "郜", "黎", "蓟", "溥", "印", "宿", "白", "怀", "蒲", "邰", "从", "鄂", "索", "咸", "籍", "赖", "卓", "蔺", "屠",
            "蒙", "池", "乔", "阴", "郁", "胥", "能", "苍", "双", "闻", "莘", "党", "翟", "谭", "贡", "劳", "逄", "姬", "申", "扶", "堵", "冉", "宰", "郦", "雍", "却",
            "璩", "桑", "桂", "濮", "牛", "寿", "通", "边", "扈", "燕", "冀", "浦", "尚", "农", "温", "别", "庄", "晏", "柴", "瞿", "阎", "充", "慕", "连", "茹", "习",
            "宦", "艾", "鱼", "容", "向", "古", "易", "慎", "戈", "廖", "庾", "终", "暨", "居", "衡", "步", "都", "耿", "满", "弘", "匡", "国", "文", "寇", "广", "禄",
            "阙", "东", "欧", "殳", "沃", "利", "蔚", "越", "夔", "隆", "师", "巩", "厍", "聂", "晁", "勾", "敖", "融", "冷", "訾", "辛", "阚", "那", "简", "饶", "空",
            "曾", "毋", "沙", "乜", "养", "鞠", "须", "丰", "巢", "关", "蒯", "相", "查", "后", "荆", "红", "游", "郏", "竺", "权", "逯", "盖", "益", "桓", "公", "仉",
            "督", "岳", "帅", "缑", "亢", "况", "郈", "有", "琴", "归", "海", "晋", "楚", "闫", "法", "汝", "鄢", "涂", "钦", "商", "牟", "佘", "佴", "伯", "赏", "墨",
            "哈", "谯", "篁", "年", "爱", "阳", "佟", "言", "福", "南", "火", "铁", "迟", "漆", "官", "冼", "真", "展", "繁", "檀", "祭", "密", "敬", "揭", "舜", "楼",
            "疏", "冒", "浑", "挚", "胶", "随", "高", "皋", "原", "种", "练", "弥", "仓", "眭", "蹇", "覃", "阿", "门", "恽", "来", "綦", "召", "仪", "风", "介", "巨",
            "木", "京", "狐", "郇", "虎", "枚", "抗", "达", "杞", "苌", "折", "麦", "庆", "过", "竹", "端", "鲜", "皇", "亓", "老", "是", "秘", "畅", "邝", "还", "宾",
            "闾", "辜", "纵", "侴", "万俟", "司马", "上官", "欧阳", "夏侯", "诸葛", "闻人", "东方", "赫连", "皇甫", "羊舌", "尉迟", "公羊", "澹台", "公冶", "宗正",
            "濮阳", "淳于", "单于", "太叔", "申屠", "公孙", "仲孙", "轩辕", "令狐", "钟离", "宇文", "长孙", "慕容", "鲜于", "闾丘", "司徒", "司空", "兀官", "司寇",
            "南门", "呼延", "子车", "颛孙", "端木", "巫马", "公西", "漆雕", "车正", "壤驷", "公良", "拓跋", "夹谷", "宰父", "谷梁", "段干", "百里", "东郭", "微生",
            "梁丘", "左丘", "东门", "西门", "南宫", "第五", "公仪", "公乘", "太史", "仲长", "叔孙", "屈突", "尔朱", "东乡", "相里", "胡母", "司城", "张廖", "雍门",
            "毋丘", "贺兰", "綦毋", "屋庐", "独孤", "南郭", "北宫", "王孙"};
    //手机号前缀
    private static final String[] preNum = {"134", "135", "136", "137", "138", "139", "150", "151", "152", "157", "158", "159"};

    private static Random random = new Random(System.currentTimeMillis());

    private List<CarBean> carBeans = new ArrayList<CarBean>();

    public Trie trie;

    public CarData() {
        trie = new Trie();
    }

    /**
     * 设置车辆数据
     */
    public List<CarBean> getCarData() {
        for (int i = 1; i <= 10000; i++) {
            CarBean carBean = new CarBean();
            carBean.setCarID(Integer.toString(i));
            carBean.setBrand(randomCarBrand());
            carBean.setModel(randomCarModel(carBean.getBrand()));
            carBean.setNumber(getRandomCarNumBer());
            carBean.setOwner(randomCarOwnerName());
            carBean.setPhone(getRandomPhoneNumber());
            carBeans.add(carBean);
        }
        return carBeans;
    }

    /**
     * 随机生成品牌
     */
    private String randomCarBrand() {
        int number = random.nextInt(10);
        return carBrands[number];
    }

    /**
     * 根据车品牌随机生成车型号
     */
    private String randomCarModel(String brand) {
        int index = 0;
        boolean isFound = false;
        //寻找车品牌的下标
        for (int i = 0; i < carBrands.length; i++) {
            if (brand.equals(carBrands[i])) {
                index = i;
                isFound = true;
                break;
            }
        }
        if (!isFound) {
            try {
                throw new Exception("没有这个车品牌");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        int length = carModels[index].length;
        //生成 0 至 length - 1的随机数
        int number = random.nextInt(length);
        return carModels[index][number];
    }

    /**
     * 随机生成车牌号,需要保证唯一性
     */
    private String randomCarNumber() {
        //获取省编号
        int provinceIndex = random.nextInt(provinces.length);
        String province = provinces[provinceIndex];
        //获取市编号
        int cityIndex = random.nextInt(cities.length);
        String city = cities[cityIndex];
        //获取后四位
        isChar = true;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            stringBuilder.append(getNum());
        }
        String carNumber = province + city + stringBuilder.toString();
        return carNumber;
    }

    /**
     * 获取后四位
     */
    private String getNum() {
        String num;
        //如果前一位是字母的话，那么下一位可以是字母或者是数字
        //如果前一位不是字母的话，那么后面的都只能是数字
        if (isChar) {
            int bounds = random.nextInt(10000) + 1;
            if (bounds > 9990) {
                int cityIndex = random.nextInt(cities.length);
                num = cities[cityIndex];
                isChar = true;
            } else {
                num = Integer.toString(random.nextInt(10));
                isChar = false;
            }
        } else {
            num = Integer.toString(random.nextInt(10));
        }
        return num;
    }

    /**
     * 使用前缀树来保证唯一的电话号码
     *
     * @return
     */
    private String getRandomCarNumBer() {
        String carNumber;
        while (true) {
            carNumber = randomCarNumber();
            if (!trie.isIn(carNumber)) {
                trie.insert(carNumber);
                break;
            }
        }
        return carNumber;
    }

    /**
     * 随机生成车主名字,不需要保证唯一性，但是要保证重复率低
     */
    private String randomCarOwnerName() {
        // 随机取得一个姓氏
        int index = random.nextInt(firstName.length);
        String name = firstName[index];
        // nextBoolean() 方法用于从该随机数生成器的序列得到下一个伪均匀分布的布尔值。
        // 根据随机布尔值，确定名字是一个字还是两个字
        if (random.nextBoolean()) {
            name += getName() + getName();
        } else {
            name += getName();
        }
        return name;
    }

    /**
     * 随机获取汉字来组成名字
     *
     * @return
     */
    public static String getName() {
        String nameStr = "";
        int highCode, lowCode;
        // 区码，0xA0打头，从第16区开始，即0xB0=11*16=176,16~55一级汉字，56~87二级汉字
        highCode = (176 + Math.abs(random.nextInt(71)));
        random = new Random();
        // 位码，0xA0打头，范围第1~94列
        lowCode = 161 + Math.abs(random.nextInt(94));

        byte[] codeArr = new byte[2];
        codeArr[0] = (new Integer(highCode)).byteValue();
        codeArr[1] = (new Integer(lowCode)).byteValue();
        try {
            // 区位码组合成汉字
            nameStr = new String(codeArr, "GB2312");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return nameStr;
    }


    /**
     * 随机生成手机号,需要保证唯一性
     */
    private String randomPhoneNumber() {
        StringBuilder stringBuilder = new StringBuilder();
        //随机生成电话号码后8位
        for (int i = 0; i < 8; i++) {
            int number = random.nextInt(10);
            stringBuilder.append(number);
        }

        //随机获取前三位
        int index = random.nextInt(preNum.length);
        String pre = preNum[index];
        //拼接电话号码
        String phone = pre + stringBuilder.toString();
        return phone;
    }

    /**
     * 使用前缀树来保证唯一的电话号码
     *
     * @return
     */
    private String getRandomPhoneNumber() {
        String phone;
        while (true) {
            phone = randomPhoneNumber();
            //如果phone不在前缀树中，则插入后break，否则继续循环
            if (!trie.isIn(phone)) {
                trie.insert(phone);
                break;
            }
        }
        return phone;
    }
}
