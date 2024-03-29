package Task.StudentManager;

public class Graduate extends Student {
    String major;

    public Graduate(String number,String course, String name,  int year, String professor,String address, double GPA, String major) {
        super(number, course,name, year,professor, address, GPA);
        this.major = major;
    }

    public Graduate(String number, String course,String name, int year,  String professor,String address, double GPA) {
        super(number, course,name, year,professor, address, GPA);
    }

    public String Major() {
        return major;
    }

    public void Major(String major){
        this.major = major;
    }

    @Override
    public void show() {
        System.out.println(String.format("%s,%s,%s,%d,%s,%s,%2f", number, course, name, year, professor, address,
        GPA) + (major == null ? "" : ","+major));
    }

}