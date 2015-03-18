package listView;

/**
 * Created by martin_w on 18.03.2015.
 */
public class ItemEditions implements ItemContent, Comparable<ItemEditions> {
    private String edition;
    private int editionNr;
    private int year;
    private String isbn;

    public ItemEditions(String edition, int year, String isbn){
        this.edition = edition;
        this.year = year;
        this.isbn = isbn;
        setEditionNr(edition);
    }

    private void setEditionNr(String edition){

        try {
            editionNr = Integer.parseInt(edition.substring(0,2));
        }catch (NumberFormatException e){
            try {
                editionNr = Integer.parseInt(edition.substring(0,1));
            }catch (NumberFormatException e2){
                editionNr = 0;
            }
        }
    }

    @Override
    public String getShowText1() {
        return edition;
    }

    @Override
    public String getShowText2() {
        String text = "year: " + year + "\n" + "isbn: " + isbn;
        return  text;
    }

    @Override
    public int compareTo(ItemEditions another) {
        if(editionNr < another.editionNr){
            return 1;
        }else if(editionNr > another.editionNr){
            return -1;
        }else {
            if(year < another.year){
                return 1;
            }
            else if( year > another.year){
                return -1;
            }
            else{
                return 0;
            }
        }


    }
}
