package co.rpoint.www.rpoint;

/**
 * Created by georg on 7/19/2017.
 */

public class Test {

    public int id;
    public String result;
    public int age;
    public int weight;
    public String gender;
    public Test(int id, float result, int age, int weight, String gender)
    {
        this.id=id;
        this.result=result+"";
        this.age=age;
        this.weight=weight;
        this.gender=gender;
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
