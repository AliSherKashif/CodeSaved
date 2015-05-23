package com.cygnus.honda.listview.adapters;

import java.util.ArrayList;

import com.cygnus.honda.R;
import com.cygnus.honda.listview.section.Contact;
import com.cygnus.honda.listview.section.ContactItem;
import com.cygnus.honda.listview.section.Message;
import com.cygnus.honda.listview.section.MessageItem;
import com.cygnus.honda.listview.section.SectionItem;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView.FindListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MessagesAdapter extends ArrayAdapter<Message> {

	private Context context;
	private ArrayList<Message> messagesItems;
	private LayoutInflater vi;
	private boolean checkMode;

	public MessagesAdapter(Context context, ArrayList<Message> messagesItems,
			boolean checkMode) {
		super(context, 0, messagesItems);
		this.context = context;
		this.checkMode = checkMode;
		this.messagesItems = messagesItems;
		vi = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		final Message i = messagesItems.get(position);
		if (i != null) {
			if (i.isSection()) {
				// SectionItem si = (SectionItem) i;

				MessageItem messageItem = (MessageItem) i;
				rowView = vi.inflate(R.layout.my_message_single_list, null);

				final TextView arrowTV = (TextView) rowView
						.findViewById(R.id.user_message_arrow);

				final RelativeLayout messageBG = (RelativeLayout) rowView
						.findViewById(R.id.relativelayout_message_date);

				if (checkMode) {

					arrowTV.setBackgroundDrawable(context.getResources()
							.getDrawable(R.drawable.my_part_day));

					messageBG.setBackgroundDrawable(context.getResources()
							.getDrawable(R.drawable.user_messges_bg));

				} else {

					arrowTV.setBackgroundDrawable(context.getResources()
							.getDrawable(R.drawable.my_part_night));

					messageBG.setBackgroundDrawable(context.getResources()
							.getDrawable(R.drawable.user_message_bg_night));

				}

				final TextView myMessage = (TextView) rowView
						.findViewById(R.id.user_message_TV);

				final TextView date = (TextView) rowView
						.findViewById(R.id.user_message_TV);

				String[] dateArray = messageItem.date.split("at");

				date.setText("at " + dateArray[1] + " " + dateArray[0]);

				myMessage.setText(messageItem.message);

				// sectionView.setText(si.getTitle().toString());
				// sectionView.setTextColor(context.getResources().getColor(
				// R.color.light_black));
				// sectionView.setGravity(Gravity.CENTER);

			} else {

				// ///////////////////// messaging part is here
				// /////////////////
				MessageItem messageItem = (MessageItem) i;

				if (messageItem.checkVal) {
					rowView = vi.inflate(R.layout.sender_message_single_list,
							null);

					final TextView arrowTV = (TextView) rowView
							.findViewById(R.id.user_message_arrow);

					final RelativeLayout messageBG = (RelativeLayout) rowView
							.findViewById(R.id.relativelayout_message_date);

					if (checkMode) {

						arrowTV.setBackgroundDrawable(context.getResources()
								.getDrawable(R.drawable.sender_part_day));

						messageBG.setBackgroundDrawable(context.getResources()
								.getDrawable(R.drawable.user_message_bg_night));

					} else {

						arrowTV.setBackgroundDrawable(context.getResources()
								.getDrawable(R.drawable.sender_part_night));

						messageBG.setBackgroundDrawable(context.getResources()
								.getDrawable(R.drawable.user_message_bg_night));

					}

					final TextView senderMessage = (TextView) rowView
							.findViewById(R.id.message_sender_TV);

					senderMessage.setText(messageItem.message);

					String[] dateArray = messageItem.date.split("at");

					final TextView date = (TextView) rowView
							.findViewById(R.id.message_sender_date_TV);
					date.setText("at " + dateArray[1] + " " + dateArray[0]);

				} else {

					rowView = vi.inflate(R.layout.my_message_single_list, null);

					final TextView arrowTV = (TextView) rowView
							.findViewById(R.id.user_message_arrow);

					final RelativeLayout messageBG = (RelativeLayout) rowView
							.findViewById(R.id.relativelayout_message_date);

					final TextView myMessage = (TextView) rowView
							.findViewById(R.id.user_message_TV);

					final TextView date = (TextView) rowView
							.findViewById(R.id.user_message_date_TV);

					if (checkMode) {

						arrowTV.setBackgroundDrawable(context.getResources()
								.getDrawable(R.drawable.my_part_day));

						messageBG.setBackgroundDrawable(context.getResources()
								.getDrawable(R.drawable.user_messges_bg));

					} else {

						arrowTV.setBackgroundDrawable(context.getResources()
								.getDrawable(R.drawable.my_part_night));

						messageBG.setBackgroundDrawable(context.getResources()
								.getDrawable(R.drawable.user_message_bg_night));

						myMessage.setTextColor(context.getResources().getColor(
								R.color.black));

						date.setTextColor(context.getResources().getColor(
								R.color.black));

					}

					String[] dateArray = messageItem.date.split("at");

					date.setText("at " + dateArray[1] + " " + dateArray[0]);

					myMessage.setText(messageItem.message);

				}

			}
		}
		return rowView;
	}

	public void clearAdapter() {
		messagesItems.clear();
		//notifyDataSetChanged();
	}

	public void notifyAdapter() {
		notifyDataSetChanged();
	}

}
