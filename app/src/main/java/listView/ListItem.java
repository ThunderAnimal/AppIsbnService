package listView;

import adapter.TwoTextArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.ebusiness.isbnservice.app.R;

/**
 * Created by martin_w on 18.03.2015.
 */
public class ListItem implements Item {
    private ItemContent itemContent;

    public ListItem(ItemContent itemContent) {
        this.itemContent = itemContent;
    }

    @Override
    public int getViewType() {
        return TwoTextArrayAdapter.RowType.LIST_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_item, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text1 = (TextView) view.findViewById(R.id.list_content1);
        TextView text2 = (TextView) view.findViewById(R.id.list_content2);
        text1.setText(itemContent.getShowText1());
        text2.setText(itemContent.getShowText2());

        return view;
    }
}
