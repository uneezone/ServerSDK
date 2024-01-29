package com.example.logmanager;


public class RegionCode {
    public static int findRegionCode( String region ) {
        ProvinceCode provinceCode = ProvinceCode.UNKOWN;
        int regionCode = 0;

        provinceCode = ProvinceCode.getTypeByString(region);
        if( provinceCode == null || provinceCode == ProvinceCode.UNKOWN )
            return 0;

        switch(provinceCode) {
            case SEOUL:     regionCode = Seoul.getTypeByString(region);         break;
            case GYEONGGI:  regionCode = Gyeonggi.getTypeByString(region);      break;
            case INCHEON:   regionCode = Incheon.getTypeByString(region);       break;
            case GANGWON:   regionCode = Gangwon.getTypeByString(region);       break;
            case CHUNGNAM:  regionCode = Chungnam.getTypeByString(region);      break;
            case DAEJEON:   regionCode = Daejeon.getTypeByString(region);       break;
            case CHUNGBUK:  regionCode = Chungbuk.getTypeByString(region);      break;
            case SEJONG:    break;
            case BUSAN:     regionCode = Busan.getTypeByString(region);         break;
            case ULSAN:     regionCode = Ulsan.getTypeByString(region);         break;
            case DAEGU:     regionCode = Daegu.getTypeByString(region);         break;
            case GYEONGBUK: regionCode = Gyeongbuk.getTypeByString(region);     break;
            case GYEONGNAM: regionCode = Gyeongnam.getTypeByString(region);     break;
            case GWANGJU:   regionCode = Gwangju.getTypeByString(region);       break;
            case JEONNAM:   regionCode = Jeonnam.getTypeByString(region);       break;
            case JEONBUK:   regionCode = Jeonbuk.getTypeByString(region);       break;
            case JEJU:      break;
            default: return 0;
        }

        return provinceCode.getValue() + regionCode;
    }

    public static int findOldDriverLicenseCode(String region) {
        if(region != null && !"".equals(region)) {
            return OldDriverLicenseRegionCode.getCodeByRegionName(region);
        }
        return OldDriverLicenseRegionCode.UNKNOWN.value;
    }

    public enum OldDriverLicenseRegionCode {
        UNKNOWN(0, "UNKNOWN"),
        SEOUL(11, "서울"),
        BUSAN(12, "부산"),
        GYEONGGI(13, "경기"),
        GANGWON(14, "강원"),
        CHUNGBUK(15, "충북"),
        CHUNGNAM(16, "충남"),
        JEONBUK(17, "전북"),
        JEONNAM(18, "전남"),
        GYEONGBUK(19, "경북"),
        GYEONGNAM(20, "경남"),
        JEJU(21, "제주"),
        DAEGU(22, "대구"),
        INCHEON(23, "인천"),
        GWANGJU(24, "광주"),
        DAEJEON(25, "대전"),
        ULSAN(26, "울산");
        int value;
        String key;
        OldDriverLicenseRegionCode(int value, String key) {
            this.value = value;
            this.key = key;
        }
        public static int getCodeByRegionName(String region) {
            for(OldDriverLicenseRegionCode code : values()) {
                if(code.key.equalsIgnoreCase(region)) {
                    return code.value;
                }
            }
            return UNKNOWN.value;
        }
    }

    public enum ProvinceCode{
        UNKOWN(        0, "UNKOWN"),
        SEOUL(      2000, "서울"),
        GYEONGGI(   3100, "경기"),
        INCHEON(    3200, "인천"),
        GANGWON(    3300, "강원"),
        CHUNGNAM(   4100, "충청남"),
        DAEJEON(    4200, "대전"),
        CHUNGBUK(   4300, "충청북"),
        SEJONG(     4400, "세종"),
        BUSAN(      5100, "부산"),
        ULSAN(      5200, "울산"),
        DAEGU(      5300, "대구"),
        GYEONGBUK(  5400, "경상북"),
        GYEONGNAM(  5500, "경상남"),
        GWANGJU(    6200, "광주"),
        JEONNAM(    6100, "전라남"),
        JEONBUK(    6300, "전라북"),
        JEJU(       6400, "제주");

        private final int enumNmber;
        private final String enumKey;

        ProvinceCode(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() {
            return enumNmber;
        }
        public String getStringValue() {
            return enumKey;
        }

        public static ProvinceCode getTypeByString(String str) {
            for( ProvinceCode code : values() ){
                if( str.contains(code.getStringValue()) ) return code;
            }
            return null;
        }
    }

    public enum Seoul {
        UNKOWN(          0, "UNKOWN"),
        YONGSANGU(       1, "용산"),
        MAPOGU(          2, "마포"),
        GWANGJINGU(      3, "광진"),
        SONGPAGU(        4, "송파"),
        DOBONGGU(        5, "도봉"),
        YEONGDEUNGPOGU(  6, "영등포"),
        GWANAKGU(        7, "관악"),
        NOWONGU(         8, "노원"),
        EUNPYEONGGU(     9, "은평"),
        JONGNOGU(       10, "종로"),
        GUROGU(         11, "구로"),
        SEODAEMUNGU(    12, "서대문"),
        SEOCHOGU(       13, "서초"),
        GANGDONGGU(     14, "강동"),
        GANGBUKGU(      15, "강북"),
        GANGSEOGU(      16, "강서"),
        GANGNAMGU(      17, "강남"),
        DONGJAKGU(      18, "동작"),
        DONGDAEMUNGU(   19, "동대문"),
        SEONGBUKGU(     20, "성북"),
        SEONGDONGGU(    21, "성동"),
        JUNGGUGU(       22, "중구"),
        JUNGNANGGU(     23, "중랑"),
        YANGCHEONGU(    24, "양천"),
        GEUMCHEONGU(    25, "금천");

        private final int enumNmber;
        private final String enumKey;

        Seoul(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Busan {
        UNKOWN(      0, "UNKOWN"),
        SEOGUGU(     1, "서구"),
        DONGGUGU(    2, "동구"),
        NAMGUGU(     3, "남구"),
        BUKGUGU(     4, "북구"),
        YEONGDOGU(   5, "영도"),
        BUSANJINGU(  6, "부산진"),
        DONGNAEGU(   7, "동래"),
        HAEUNDAEGU(  8, "해운대"),
        YEONJAEGU(   9, "연재"),
        SUYEONGGU(  10, "수영"),
        GEUMJONGGU( 11, "금정"),
        GIJANGGUN(  12, "기장"), // 군
        SASANGGU(   13, "사상"),
        SASHGU(     14, "사하");

        private final int enumNmber;
        private final String enumKey;

        Busan(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Incheon {
        UNKOWN(      0, "UNKOWN"),
        JUNGGU(      1, "중구"),
        SEOGU(       2, "서구"),
        DONGGU(      3, "동구"),
        NAMGU(       4, "남구"),
        YEONSUGU(    5, "연수구"),
        NAMDONGGU(   6, "남동구"),
        BUPYEONGGU(  7, "부평구"),
        GYEYANGGU(   8, "계양구"),
        GANGHWAGUN(  9, "강화군"),
        ONGJINGUN(  10, "옹진군");

        private final int enumNmber;
        private final String enumKey;

        Incheon(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Gwangju {
        UNKOWN(     0, "UNKOWN"),
        SEOGU(      1, "서구"),
        DONGGU(     2, "동구"),
        NAMGU(      3, "남구"),
        BUKGU(      4, "북구"),
        GWANGSANGU( 5, "광산구");

        private final int enumNmber;
        private final String enumKey;

        Gwangju(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Daegu {
        UNKOWN(         0, "UNKOWN"),
        JONGGU(         1, "중구"),
        SEOGU(          2, "서구"),
        DONGGU(         3, "동구"),
        NAMGU(          4, "남구"),
        BUKGU(          5, "북구"),
        SUSEONGGU(      6, "수성구"),
        DALSEOGU(       7, "달서구"),
        DALSEONGGUN(    8, "달성군");

        private final int enumNmber;
        private final String enumKey;

        Daegu(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Daejeon {
        UNKOWN(     0, "UNKOWN"),
        JONGGU(     1, "중구"),
        SEOGU(      2, "서구"),
        DONGGU(     3, "동구"),
        YUSEONGGU(  4, "유성구"),
        DAEDEOKGU(  5, "대덕구");

        private final int enumNmber;
        private final String enumKey;

        Daejeon(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Ulsan {
        UNKOWN(     0, "UNKOWN"),
        JUNGGU(     1, "중구"),
        DONGGU(     2, "동구"),
        NAMGU(      3, "남구"),
        BUKGU(      4, "북구"),
        ULJUGUN(    5, "울주군");

        private final int enumNmber;
        private final String enumKey;

        Ulsan(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Gyeonggi {
        UNKOWN(          0, "UNKOWN"),
        SUWON(           1, "수원시"),
        PYEONGTAEK(      2, "평택시"),
        DONGDUCHEON(     3, "동두천시"),
        GURI(            4, "구리시"),
        SIHEUNG(         5, "시흥시"),
        SEONGNAM(        6, "성남시"),
        BUCHEON(         7, "부천시"),
        GWACHEON(        8, "과천시"),
        ICHEON(          9, "이천시"),
        POCHEON(        10, "포천시"),
        YEONCHEON(      11, "연천군"),
        GOYANG(         12, "고양시"),
        NAMYANGJU(      13, "남양주시"),
        OSAN(           14, "오산시"),
        GUNPO(          15, "군포시"),
        HANAM(          16, "하남시"),
        YONGIN(         17, "용인시"),
        PAJU(           18, "파주시"),
        GIMPO(          19, "김포시"),
        HWASEONG(       20, "화성시"),
        GAPYEONG(       21, "가평군"),
        YEOJU(          22, "여주시"),
        GWANGMYEONG(    23, "광명시"),
        GWANGJU(        24, "광주시"),
        YANGPYEONG(     25, "양평군"),
        YANGJU(         26, "양주시"),
        UIJEONGBU(      27, "의정부시"),
        UIWANG(         28, "의왕시"),
        ANYANG(         29, "안양시"),
        ANSAN(          30, "안산시"),
        ANSEONG(        31, "안성시");

        private final int enumNmber;
        private final String enumKey;

        Gyeonggi(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Gangwon {
        UNKOWN(          0, "UNKOWN"),
        CHUNCHEON(       1, "춘천시"),
        WONJU(           2, "원주시"),
        GANGNEUNG(       3, "강릉시"),
        DONGHAE(         4, "동해시"),
        TAEBAEK(         5, "태백시"),
        SOKCHO(          6, "속초시"),
        SAMCHEOK(        7, "삼척시"),
        HONGCHEON(       8, "홍천군"),
        HOENGSEONG(      9, "횡성군"),
        YEONGWOL(       10, "영월군"),
        PYEONGCHANG(    11, "평창군"),
        JEONGSEON(      12, "정선군"),
        CHEOLWON(       13, "철원군"),
        HWACHEON(       14, "화천군"),
        YANGGU(         15, "양구군"),
        INJE(           16, "인제군"),
        GOSEONG(        17, "고성군"),
        YANGYANG(       18, "양양군");

        private final int enumNmber;
        private final String enumKey;

        Gangwon(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Chungbuk {
        UNKOWN(          0, "UNKOWN"),
        CHEONGJU(        1, "청주시"),
        CHUNGJU(         2, "충주시"),
        JECHEON(         3, "제천시"),
        BOEUN(           4, "보은군"),
        OGCHEON(         5, "옥천군"),
        YEONGDONG(       6, "영동군"),
        JINCHEON(        7, "진천군"),
        GOESAN(          8, "괴산군"),
        EUMSEONG(        9, "음성군"),
        DANYANG(        10, "단양군");

        private final int enumNmber;
        private final String enumKey;

        Chungbuk(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Chungnam {
        UNKOWN(          0, "UNKOWN"),
        CHEONAN(         1, "천안시"),
        GONGJU(          2, "공주시"),
        BORYEONG(        3, "보령시"),
        ASAN(            4, "아산시"),
        SEOSAN(          5, "서산시"),
        NONSAN(          6, "논산시"),
        GYERYONG(        7, "계룡시"),
        DANGJIN(         8, "당진시"),
        GEUMSAN(         9, "금산군"),
        BUYEO(          10, "부여군"),
        SEOCHEON(       11, "서천군"),
        CHEONGYANG(     12, "청양군"),
        HONGSEONG(      13, "홍성군"),
        YESAN(          14, "예산군"),
        TAEAN(          15, "태안군");

        private final int enumNmber;
        private final String enumKey;

        Chungnam(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Jeonbuk {
        UNKOWN(          0, "UNKOWN"),
        JEONJU(          1, "전주시"),
        GUNSAN(          2, "군산시"),
        IKSAN(           3, "익산시"),
        JEONGEUP(        4, "정읍시"),
        NAMWON(          5, "남원시"),
        GIMJE(           6, "김제시"),
        WANJU(           7, "완주군"),
        JINAN(           8, "진안군"),
        MUJU(            9, "무주군"),
        JANGSU(         10, "장수군"),
        IMSIL(          11, "임실군"),
        SUNCHANG(       12, "순창군"),
        GOCHANG(        13, "고창군"),
        BUAN(           14, "부안군");

        private final int enumNmber;
        private final String enumKey;

        Jeonbuk(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Jeonnam {
        UNKOWN(          0, "UNKOWN"),
        MOKPO(           1, "목포시"),
        YEOSU(           2, "여수시"),
        SUNCHEON(        3, "순천시"),
        NAJU(            4, "나주시"),
        GWANGYANG(       5, "광양시"),
        DAMYANG(         6, "담양군"),
        GOKSEONG(        7, "곡성군"),
        GURYE(           8, "구례군"),
        GOHEUNG(         9, "고흥군"),
        BOSEONG(        10, "보성군"),
        HWASUN(         11, "화순군"),
        JANGHEUNG(      12, "장흥군"),
        GANGJIN(        13, "강진군"),
        HAENAM(         14, "해남군"),
        YEONGAM(        15, "영암군"),
        MUAN(           16, "무안군"),
        HAMPYEONG(      17, "함평군"),
        YEONGGWANG(     18, "영광군"),
        JANGSEONG(      19, "장성군"),
        WANDO(          20, "완도군"),
        JINDO(          21, "진도군"),
        SINAN(          22, "신안군");

        private final int enumNmber;
        private final String enumKey;

        Jeonnam(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Gyeongbuk {
        UNKOWN(          0, "UNKOWN"),
        GYEONGSAN(       1, "경산시"),
        GYEONGJU(        2, "경주시"),
        GORYEONG(        3, "고령군"),
        GUMI(            4, "구미시"),
        GUNWI(           5, "군위군"),
        GIMCHEON(        6, "김천시"),
        MUNGYEONG(       7, "문경시"),
        BONGHWA(         8, "봉화군"),
        SANGJU(          9, "상주시"),
        SEONGJU(        10, "성주군"),
        ANDONG(         11, "안동시"),
        YEONGDEOK(      12, "영덕군"),
        YEONGYANG(      13, "영양군"),
        YEONGJU(        14, "영주시"),
        YEONGCHEON(     15, "영천시"),
        YECHEON(        16, "예천군"),
        ULEUNG(         17, "울릉군"),
        ULJIN(          18, "울진군"),
        UISEONG(        19, "의성군"),
        CHEONGDO(       20, "청도군"),
        CHEONGSONG(     21, "청송군"),
        CHILGOK(        22, "칠곡군"),
        POHANG(         23, "포항시");

        private final int enumNmber;
        private final String enumKey;

        Gyeongbuk(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }

    public enum Gyeongnam {
        UNKOWN(          0, "UNKOWN"),
        CHANGWON(        1, "창원시"),
        JINJU(           2, "진주시"),
        TONGYEONG(       3, "통영시"),
        SACHEON(         4, "사천시"),
        GIMHAE(          5, "김해시"),
        MILYANG(         6, "밀양시"),
        GEOJE(           7, "거제시"),
        YANGSAN(         8, "양산시"),
        UIRYEONG(        9, "의령군"),
        HAMAN(          10, "함안군"),
        CHANGNYEONG(    11, "창녕군"),
        GOSEONG(        12, "고성군"),
        NAMHAE(         13, "남해군"),
        HADONG(         14, "하동군"),
        SANCHEONG(      15, "산청군"),
        HAMYANG(        16, "함양군"),
        GEOCHANG(       17, "거창군"),
        HABCHEON(       18, "합천군");

        private final int enumNmber;
        private final String enumKey;

        Gyeongnam(int value, String key) {
            this.enumNmber = value;
            this.enumKey = key;
        }

        public int getValue() { return enumNmber; }
        public String getStringValue() { return enumKey; }

        public static int getTypeByString(String str) {
            for( int i=0 ; i<values().length ; i++ ) {
                if( str.contains(values()[i].getStringValue()) ) return values()[i].getValue();
            }
            return UNKOWN.getValue();
        }
    }
}

