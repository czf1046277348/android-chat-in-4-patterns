package nju.androidchat.client.hw1;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import lombok.extern.java.Log;
import nju.androidchat.client.ClientMessage;
import nju.androidchat.client.R;
import nju.androidchat.client.Utils;
import nju.androidchat.client.component.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log
public class Hw1TalkActivity extends AppCompatActivity implements Mvp0Contract.View, TextView.OnEditorActionListener, OnRecallMessageRequested {

    private Mvp0Contract.Presenter presenter;
    private Mvp0Contract.PicPresenter picPresenter;

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            UUID messageID=null;
            switch (msg.what) {
                case 1:
                    messageID=UUID.fromString(msg.getData().getString("messageID"));
                    addSendPic((Bitmap) msg.obj,messageID);
                    break;
                case 2:
                    messageID=UUID.fromString(msg.getData().getString("messageID"));
                    addReceivePic((Bitmap) msg.obj,messageID);
                    break;
                default:
                    break;
            }
        }
    };

    public void addSendPic(Bitmap bitmap,UUID messageId){
        LinearLayout content = findViewById(R.id.chat_content);
        content.addView(new ItemPicSend(this,bitmap,messageId));
    }

    public void addReceivePic(Bitmap bitmap,UUID messageId){
        LinearLayout content = findViewById(R.id.chat_content);
        content.addView(new ItemPicReceive(this,bitmap,messageId));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Mvp0TalkModel mvp0TalkModel = new Mvp0TalkModel();

        // Create the presenter
        this.presenter = new Mvp0TalkPresenter(mvp0TalkModel, this, new ArrayList<>());
        this.picPresenter=new Mvp0PicPersenter();
        mvp0TalkModel.setIMvp0TalkPresenter(this.presenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.start();
    }

    @Override
    public void showMessageList(List<ClientMessage> messages) {
        runOnUiThread(() -> {
                    LinearLayout content = findViewById(R.id.chat_content);

                    // 删除所有已有的ItemText
                    content.removeAllViews();

                    // 增加ItemText
                    for (ClientMessage message : messages) {
                        String text = String.format("%s", message.getMessage());
                        if(text.length()>5&&text.substring(0,2).equals("![")&&text.contains("](")&&text.charAt(text.length()-1)==')'){
                            log.info("------------------------图片");
                            String url = text.substring(text.indexOf('(') + 1, text.lastIndexOf(')'));
                            if(url.substring(0,4).equals("http")){
                                log.info("--------------------进来了");
                                if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                                    this.picPresenter.showPic(url,message.getMessageId(),handler,1);
                                }else{
                                    this.picPresenter.showPic(url,message.getMessageId(),handler,2);
                                }
                            }else{
                                if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                                    content.addView(new ItemTextSend(this, "(图片格式错误，无法解析)", message.getMessageId(), this));
                                }else{
                                    content.addView(new ItemTextReceive(this, "(图片格式错误，无法解析)", message.getMessageId()));
                                }
                            }
                        }else {
                            log.info("----------------------文字");
                            if (message.getSenderUsername().equals(this.presenter.getUsername())) {
                                content.addView(new ItemTextSend(this, text, message.getMessageId(), this));
                            } else {
                                content.addView(new ItemTextReceive(this, text, message.getMessageId()));
                            }
                        }
                    }

                    Utils.scrollListToBottom(this);
                }
        );
    }

    @Override
    public void setPresenter(Mvp0Contract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            return hideKeyboard();
        }
        return super.onTouchEvent(event);
    }

    private boolean hideKeyboard() {
        return Utils.hideKeyboard(this);
    }


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (Utils.send(actionId, event)) {
            hideKeyboard();
            // 异步地让Controller处理事件
            sendText();
        }
        return false;
    }

    private void sendText() {
        EditText text = findViewById(R.id.et_content);
        AsyncTask.execute(() -> {
            this.presenter.sendMessage(text.getText().toString());
        });
    }

    public void onBtnSendClicked(View v) {
        hideKeyboard();
        sendText();
    }

    // 当用户长按消息，并选择撤回消息时做什么，MVP-0不实现
    @Override
    public void onRecallMessageRequested(UUID messageId) {

    }
}
