package net.trejj.rubytalksense.model.realms;

import net.trejj.rubytalksense.model.constants.DBConstants;
import net.trejj.rubytalksense.model.realms.Status;
import net.trejj.rubytalksense.model.realms.User;
import net.trejj.rubytalksense.utils.TimeHelper;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;
import io.realm.internal.Keep;

@Keep
public class UserStatuses extends RealmObject {
    @PrimaryKey
    private String userId;
    private long lastStatusTimestamp;
    private net.trejj.rubytalksense.model.realms.User user;
    private RealmList<net.trejj.rubytalksense.model.realms.Status> statuses;
    private boolean areAllSeen;

    public UserStatuses(String userId, long lastStatusTimestamp, net.trejj.rubytalksense.model.realms.User user, RealmList<net.trejj.rubytalksense.model.realms.Status> statuses) {
        this.userId = userId;
        this.lastStatusTimestamp = lastStatusTimestamp;
        this.user = user;
        this.statuses = statuses;
    }

    public boolean isAreAllSeen() {
        return areAllSeen;
    }

    public void setAreAllSeen(boolean areAllSeen) {
        this.areAllSeen = areAllSeen;
    }

    public UserStatuses() {
    }

    public String getUserId() {
        return userId;
    }

    public net.trejj.rubytalksense.model.realms.User getUser() {
        return user;
    }

    public RealmResults<net.trejj.rubytalksense.model.realms.Status> getMyStatuses() {


        return statuses
                .sort(DBConstants.TIMESTAMP, Sort.DESCENDING);


    }

    //get only statuses that are not passed 24 hours from local database
    public RealmResults<net.trejj.rubytalksense.model.realms.Status> getFilteredStatuses() {
        return statuses
                .sort(DBConstants.TIMESTAMP, Sort.ASCENDING)
                .where()
                .between(DBConstants.TIMESTAMP, TimeHelper.getTimeBefore24Hours(), Long.MAX_VALUE)
                .findAll();
    }

    public RealmList<net.trejj.rubytalksense.model.realms.Status> getStatuses() {
        return statuses;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setLastStatusTimestamp(long lastStatusTimestamp) {
        this.lastStatusTimestamp = lastStatusTimestamp;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setStatuses(RealmList<Status> statuses) {
        this.statuses = statuses;
    }

    public long getLastStatusTimestamp() {
        return lastStatusTimestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof UserStatuses) {
            UserStatuses temp = (UserStatuses) o;
            if (this.userId.equals(temp.getUserId()))
                return true;
        }
        return false;
    }


}
