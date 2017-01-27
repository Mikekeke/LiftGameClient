package smartlift.ibesk.ru.smartliftclient.model;

/**
 * Created by Mikekeke on 27-Jan-17.
 */

public class Question {
    private int num;
    private String name;
    private String question;
    private Variant[] variants;
    private short correctVar;

    public short getCorrectVar() {
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

    public Variant[] getVariants() {
        return variants;
    }

    private QStatus status;
    private String img1, img2;
}
