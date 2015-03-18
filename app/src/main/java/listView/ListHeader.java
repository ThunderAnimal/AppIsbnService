package listView;

import adapter.TwoTextArrayAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.ebusiness.isbnservice.app.R;

/**
 * Created by martin_w on 18.03.2015.
 */
public class ListHeader implements Item{
    private final String name;

    public ListHeader(String name) {
        this.name = name;
    }

    @Override
    public int getViewType() {
        return TwoTextArrayAdapter.RowType.HEADER_ITEM.ordinal();
    }

    @Override
    public View getView(LayoutInflater inflater, View convertView) {
        View view;
        if (convertView == null) {
            view = (View) inflater.inflate(R.layout.list_header, null);
            // Do some initialization
        } else {
            view = convertView;
        }

        TextView text = (TextView) view.findViewById(R.id.separator);
        text.setText(name);

        return view;
    }
}
