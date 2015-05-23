package com.cygnus.honda.listview.adapters;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import com.cygnus.honda.R;
import com.cygnus.honda.listview.section.Contact;
import com.cygnus.honda.listview.section.ContactItem;
import com.cygnus.honda.listview.section.SectionItem;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ContactAdapter extends ArrayAdapter<ContactItem> {

	private Context context;
	private LayoutInflater vi;

	private List<ContactItem> worldpopulationlist = null;
	private ArrayList<ContactItem> arraylist;

	SharedPreferences pref;

	public ContactAdapter(Context context,
			ArrayList<ContactItem> worldpopulationlist, SharedPreferences pref) {
		super(context, 0, worldpopulationlist);
		this.context = context;
		this.pref = pref;

		this.worldpopulationlist = worldpopulationlist;

		this.arraylist = new ArrayList<ContactItem>();
		this.arraylist.addAll(worldpopulationlist);

		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		rowView = vi.inflate(R.layout.contact_list_single, null);

		// contact name plus contact status TextView
		TextView contactName = (TextView) rowView
				.findViewById(R.id.contact_name_TV);
		contactName.setText(worldpopulationlist.get(position).getContacName());

		TextView contactStatus = (TextView) rowView
				.findViewById(R.id.contact_status_TV);
		contactStatus.setText(worldpopulationlist.get(position)
				.getContactStatus());

		// set row bg

		RelativeLayout rowRL = (RelativeLayout) rowView
				.findViewById(R.id.row_bg);
		boolean isConfirmed = worldpopulationlist.get(position)
				.getIsConfirmed();

		if (isConfirmed) {

		} else {
			rowRL.setBackgroundColor(context.getResources().getColor(
					R.color.green));
		}

		String idVal = pref.getString("refreshcontact"
				+ worldpopulationlist.get(position).getID(), "");

		if (idVal.equals("")) {

		} else {
			rowRL.setBackgroundColor(context.getResources().getColor(
					R.color.Gray));

		}

		//

		// contact Image and status Image are set here in row

		ImageView contactIV = (ImageView) rowView
				.findViewById(R.id.contact_imageView);
		contactIV.setImageResource(worldpopulationlist.get(position)
				.getContactImage());

		ImageView contactStatatusImage = (ImageView) rowView
				.findViewById(R.id.contact_status_IV);
		contactStatatusImage.setImageResource(worldpopulationlist.get(position)
				.getStatusImage());

		return rowView;
	}

	// Filter Class
	public void filter(String charText, Button btn,
			ListView searchContactsListView) {
		charText = charText.toLowerCase(Locale.getDefault());
		worldpopulationlist.clear();
		if (charText.length() == 0) {
			worldpopulationlist.addAll(arraylist);
			// btn.setVisibility(View.INVISIBLE);
			searchContactsListView.setVisibility(View.INVISIBLE);
		} else {
			for (ContactItem wp : arraylist) {
				if (wp.getContacName().toLowerCase(Locale.getDefault())
						.contains(charText)) {
					worldpopulationlist.add(wp);
					// btn.setVisibility(View.VISIBLE);
				}
			}
		}
		notifyDataSetChanged();
	}

	public void clearAdapter1() {
		arraylist.clear();
		worldpopulationlist.clear();
		notifyDataSetChanged();
	}

	public void notifyAdapter1() {
		notifyDataSetChanged();
	}

}
