# HeartbeatAnimation
模仿小米手环心率动画
### 效果图

![模仿小米手环心率动画](https://github.com/billy96322/HeartbeatAnimation/blob/master/app/screen_capture/Animation.gif)

### 说明
- 屏幕适配为测试，不确保在所有屏幕上正常显示
- 基于属性动画实现，兼容Android 2.3及以下版本需修改使用[NineOldAndroids](https://github.com/JakeWharton/NineOldAndroids)
- 未开放属性设置震幅与周期，可以根据需要修改代码
- 关于心跳动画结束的滚动数字，[详情在此](https://github.com/billy96322/DigitView)
- 颜色素材及圆环转动进度部分实现参考[此项目](https://github.com/xueerfei/MIRing)
- 后续更新将会实现分离控制两个动画，即心率动画继续播放，圆环进度动画可以暂停

### 使用

布局文件中添加
```
<com.salmonzhg.heartbeatview.views.HeartbeatView
    android:id="@+id/heartbeat"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_centerInParent="true"
    android:layout_gravity="center_horizontal"/>
```

在Activity中添加如下代码播放或者停止
```
// 播放动画
mHeartbeatView.startAnim();
// 停止动画
mHeartbeatView.stopAnim();
```

动画播放结束回掉
```
mHeartbeatView.setHeartBeatAnimListener(new HeartbeatView.HeartBeatAnimImpl() {
    @Override
    public void onAnimFinished() {
        // do something
    }
});
```
