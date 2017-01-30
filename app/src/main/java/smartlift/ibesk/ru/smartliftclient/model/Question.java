package smartlift.ibesk.ru.smartliftclient.model;

import java.io.Serializable;
import java.util.Map;

/**
 * Created by Mikekeke on 27-Jan-17.
 */

public class Question implements Serializable{
    private int num;
    private String name;
    private String question, answer;
    private Map<Integer, Variant> variants;
    private int correctVar;

    public int getCorrectVar() {
        return correctVar;
    }

    public String getImg1() {
        return img1;
    }

    public String getImg2() {
        return img2;
    }

    public String getName() {
        return name;
    }

    public int getNum() {
        return num;
    }

    public String getQuestion() {
        return question;
    }

    public QStatus getStatus() {
        return status;
    }

    public Map<Integer, Variant> getVariants() {
        return variants;
    }

    private QStatus status;
    private String img1, img2;

    public String getAnswer() {
        return answer;
    }
}
