package co.rpoint.www.rpoint;

/**
 * Created by georg on 7/19/2017.
 */

public class Test {

    public int id;
    public String result;
    public int age;
    public String weight;
    public String gender;
    public String freq;
    public Test(int id, float result, int age, double weight, int gender, String freq)
    {
        this.id=id;
        this.result=result+"";
        this.age=age;
        this.weight=weight+"";
        if(gender==0)
            this.gender="Male";
        else
            this.gender="Female";
        this.freq=freq;
    }

    public Test(int id, float result)
    {
        this.id=id;
        this.result=result+"";
    }

    public int GetId()
    {
        return this.id;
    }


}
