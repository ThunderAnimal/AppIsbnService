package listView;

/**
 * Created by martin_w on 18.03.2015.
 */
public class ItemIsbnData implements ItemContent {
    private String text;
    private String value;

    public ItemIsbnData(String text, String value){
        this.text = text;
        this.value = value;
    }

    @Override
    public String getShowText1() {
        return text;
    }

    @Override
    public String getShowText2() {
        return value;
    }
}
