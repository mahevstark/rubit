package net.trejj.rubytalksense.utils;

import net.trejj.rubytalksense.model.realms.TextStatus;
import net.trejj.rubytalksense.model.constants.StatusType;
import net.trejj.rubytalksense.model.realms.Status;
import net.trejj.rubytalksense.utils.BitmapUtils;
import net.trejj.rubytalksense.utils.FireConstants;
import net.trejj.rubytalksense.utils.MyApp;
import net.trejj.rubytalksense.utils.RealmHelper;
import net.trejj.rubytalksense.utils.Util;
import net.trejj.rubytalksense.utils.network.FireManager;

import java.util.Date;

public class StatusCreator {
    public static Status createImageStatus(String imagePath) {
        String statusId = net.trejj.rubytalksense.utils.FireConstants.getMyStatusRef(StatusType.IMAGE).push().getKey();
        String thumbImg = net.trejj.rubytalksense.utils.BitmapUtils.decodeImage(imagePath, false);
        Status status = new Status(statusId, FireManager.getUid(), new Date().getTime(), thumbImg, null, imagePath, StatusType.IMAGE);
        net.trejj.rubytalksense.utils.RealmHelper.getInstance().saveObjectToRealm(status);
        return status;
    }

    public static Status createVideoStatus(String videoPath) {
        String statusId = net.trejj.rubytalksense.utils.FireConstants.getMyStatusRef(StatusType.VIDEO).push().getKey();
        String thumbImg = BitmapUtils.generateVideoThumbAsBase64(videoPath);
        long mediaLengthInMillis = Util.getMediaLengthInMillis(MyApp.context(), videoPath);
        Status status = new Status(statusId, FireManager.getUid(), new Date().getTime(), thumbImg, null, videoPath, StatusType.VIDEO, mediaLengthInMillis);
        net.trejj.rubytalksense.utils.RealmHelper.getInstance().saveObjectToRealm(status);
        return status;
    }

    public static Status createTextStatus(TextStatus textStatus) {
        String statusId = FireConstants.getMyStatusRef(StatusType.TEXT).push().getKey();
        Status status = new Status(statusId, FireManager.getUid(), new Date().getTime(), textStatus,StatusType.TEXT);
        textStatus.setStatusId(statusId);
        RealmHelper.getInstance().saveObjectToRealm(status);
        return status;
    }
}
