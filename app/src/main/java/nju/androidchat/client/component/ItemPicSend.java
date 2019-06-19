package nju.androidchat.client.component;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.StyleableRes;
import nju.androidchat.client.R;

import java.util.UUID;

public class ItemPicSend extends LinearLayout{
    @StyleableRes
    int index0 = 0;

    private ImageView imageView;
    private Context context;
    private UUID messageId;

    public ItemPicSend(Context context, Bitmap bitmap,UUID messageId) {
        super(context);
        this.context = context;
        inflate(context, R.layout.item_pic_send, this);
        this.imageView = findViewById(R.id.chat_item_content_pic);
        this.imageView.setImageBitmap(bitmap);
        this.messageId = messageId;
    }

}
