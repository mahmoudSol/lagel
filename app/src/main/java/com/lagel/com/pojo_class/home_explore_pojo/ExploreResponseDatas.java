package com.lagel.com.pojo_class.home_explore_pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.lagel.com.pojo_class.ProductCategoryDatas;

import java.util.ArrayList;

/**
 * Created by hello on 4/5/2017.
 */
public class ExploreResponseDatas implements Parcelable {
    /*"postNodeId":246,
"mainUrl":"http://res.cloudinary.com/dr49tdlw2/image/upload/v1491316748/xsoasth0slbsoed9kyfx.jpg",
"usersTaggedInPosts":null,
"place":"NH21, Kullu, Kullu and Manali, Himachal Pradesh 175101, India",
"latitude":31.957851,
"longitude":77.109459,
"thumbnailImageUrl":"http://res.cloudinary.com/dr49tdlw2/image/upload/w_150,h_150/v1491316748/xsoasth0slbsoed9kyfx.jpg",
"postId":1491316749735,
"hashTags":"null",
"postCaption":null,
"postedByUserNodeId":46,
"postedByUserName":"jayz",
"profilePicUrl":"defaultUrl",
"postsType":0,
"postedOn":1491316749289,
"postedByUserEmail":"jayz@gmail.com",
"containerWidth":640,
"containerHeight":638,
"postedByUserFullName":"Jay",
"businessProfile":1,
"hasAudio":0,
"condition":"New(never used)",
"negotiable":1,
"likeStatus":1,
"taggedUserCoordinates":null,
"category":"fashion and accessories",
"subCategory":"null",
"productUrl":null,
"price":0,
"currency":"INR",
"productName":"test",
"likes":7,
"likedByUsers":[],
"totalComments":0,
"commentData":[]*/

    private String memberMqttId = "", postNodeId = "", mainUrl = "", usersTaggedInPosts = "", place = "", latitude = "", longitude = "", thumbnailImageUrl = "", postId = "", membername = "", isPromoted,
            hashTags = "", postCaption = "", postedByUserNodeId = "", postedByUserName = "", profilePicUrl, postsType = "", postedOn = "", postedByUserEmail = "",
            containerWidth = "", containerHeight = "", postedByUserFullName = "", businessProfile = "", hasAudio = "", condition = "", negotiable = "", likeStatus = "", taggedUserCoordinates = "",
            category = "", subCategory = "", productUrl = "", price = "", minInstallationPrice = "", maxInstallationPrice = "", minWigPrice = "", maxWigPrice = "", salon = "", currency = "", productName = "", productUsed = "", likes = "",
            totalComments = "", clickCount = "", description = "", followRequestStatus = "", memberProfilePicUrl, imageUrl1 = "", thumbnailUrl1 = "", containerHeight1 = "", containerWidth1 = "", imageUrl2 = "", thumbnailUrl2 = "", containerHeight2 = "", containerWidth2 = "", imageUrl3 = "", thumbnailUrl3 = "", containerHeight3 = "", containerWidth3 = "", thumbnailUrl4 = "", imageUrl4 = "", containerHeight4 = "", containerWidth4 = "";

    private ArrayList<ExploreLikedByUsersDatas> likedByUsers;
    private ArrayList<ProductCategoryDatas> categoryData;
    private boolean isToRemoveHomeItem = false;


    public ExploreResponseDatas() {
    }

    protected ExploreResponseDatas(Parcel in) {
        postNodeId = in.readString();
        mainUrl = in.readString();
        usersTaggedInPosts = in.readString();
        place = in.readString();
        latitude = in.readString();
        longitude = in.readString();
        thumbnailImageUrl = in.readString();
        postId = in.readString();
        membername = in.readString();
        isPromoted = in.readString();
        hashTags = in.readString();
        postCaption = in.readString();
        postedByUserNodeId = in.readString();
        postedByUserName = in.readString();
        profilePicUrl = in.readString();
        postsType = in.readString();
        postedOn = in.readString();
        postedByUserEmail = in.readString();
        containerWidth = in.readString();
        containerHeight = in.readString();
        postedByUserFullName = in.readString();
        businessProfile = in.readString();
        hasAudio = in.readString();
        condition = in.readString();
        negotiable = in.readString();
        likeStatus = in.readString();
        taggedUserCoordinates = in.readString();
        category = in.readString();
        subCategory = in.readString();
        productUrl = in.readString();
        price = in.readString();
        minInstallationPrice = in.readString();
        maxInstallationPrice = in.readString();
        minWigPrice = in.readString();
        maxWigPrice = in.readString();
        salon = in.readString();
        currency = in.readString();
        productName = in.readString();
        productUsed = in.readString();
        likes = in.readString();
        totalComments = in.readString();
        clickCount = in.readString();
        description = in.readString();
        followRequestStatus = in.readString();
        memberProfilePicUrl = in.readString();
        imageUrl1 = in.readString();
        thumbnailUrl1 = in.readString();
        containerHeight1 = in.readString();
        containerWidth1 = in.readString();
        imageUrl2 = in.readString();
        thumbnailUrl2 = in.readString();
        containerHeight2 = in.readString();
        containerWidth2 = in.readString();
        imageUrl3 = in.readString();
        thumbnailUrl3 = in.readString();
        containerHeight3 = in.readString();
        containerWidth3 = in.readString();
        thumbnailUrl4 = in.readString();
        imageUrl4 = in.readString();
        containerHeight4 = in.readString();
        containerWidth4 = in.readString();
        isToRemoveHomeItem = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(postNodeId);
        dest.writeString(mainUrl);
        dest.writeString(usersTaggedInPosts);
        dest.writeString(place);
        dest.writeString(latitude);
        dest.writeString(longitude);
        dest.writeString(thumbnailImageUrl);
        dest.writeString(postId);
        dest.writeString(membername);
        dest.writeString(isPromoted);
        dest.writeString(hashTags);
        dest.writeString(postCaption);
        dest.writeString(postedByUserNodeId);
        dest.writeString(postedByUserName);
        dest.writeString(profilePicUrl);
        dest.writeString(postsType);
        dest.writeString(postedOn);
        dest.writeString(postedByUserEmail);
        dest.writeString(containerWidth);
        dest.writeString(containerHeight);
        dest.writeString(postedByUserFullName);
        dest.writeString(businessProfile);
        dest.writeString(hasAudio);
        dest.writeString(condition);
        dest.writeString(negotiable);
        dest.writeString(likeStatus);
        dest.writeString(taggedUserCoordinates);
        dest.writeString(category);
        dest.writeString(subCategory);
        dest.writeString(productUrl);
        dest.writeString(price);
        dest.writeString(minInstallationPrice);
        dest.writeString(maxInstallationPrice);
        dest.writeString(minWigPrice);
        dest.writeString(maxWigPrice);
        dest.writeString(salon);
        dest.writeString(currency);
        dest.writeString(productName);
        dest.writeString(productUsed);
        dest.writeString(likes);
        dest.writeString(totalComments);
        dest.writeString(clickCount);
        dest.writeString(description);
        dest.writeString(followRequestStatus);
        dest.writeString(memberProfilePicUrl);
        dest.writeString(imageUrl1);
        dest.writeString(thumbnailUrl1);
        dest.writeString(containerHeight1);
        dest.writeString(containerWidth1);
        dest.writeString(imageUrl2);
        dest.writeString(thumbnailUrl2);
        dest.writeString(containerHeight2);
        dest.writeString(containerWidth2);
        dest.writeString(imageUrl3);
        dest.writeString(thumbnailUrl3);
        dest.writeString(containerHeight3);
        dest.writeString(containerWidth3);
        dest.writeString(thumbnailUrl4);
        dest.writeString(imageUrl4);
        dest.writeString(containerHeight4);
        dest.writeString(containerWidth4);
        dest.writeByte((byte) (isToRemoveHomeItem ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ExploreResponseDatas> CREATOR = new Creator<ExploreResponseDatas>() {
        @Override
        public ExploreResponseDatas createFromParcel(Parcel in) {
            return new ExploreResponseDatas(in);
        }

        @Override
        public ExploreResponseDatas[] newArray(int size) {
            return new ExploreResponseDatas[size];
        }
    };

    public void setMemberMqttId (String memberMqttId){
        this.memberMqttId = memberMqttId;
    }

    public String getMemberMqttId (){return memberMqttId;}


    public String getPostNodeId() {
        return postNodeId;
    }

    public void setPostNodeId(String postNodeId) {
        this.postNodeId = postNodeId;
    }

    public String getMainUrl() {
        return mainUrl;
    }

    public void setMainUrl(String mainUrl) {
        this.mainUrl = mainUrl;
    }

    public String getUsersTaggedInPosts() {
        return usersTaggedInPosts;
    }

    public void setUsersTaggedInPosts(String usersTaggedInPosts) {
        this.usersTaggedInPosts = usersTaggedInPosts;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getThumbnailImageUrl() {
        if (mainUrl != null && mainUrl.toLowerCase().contains("upload/")) {
            String thumbnailImage = mainUrl.replace("upload/", "upload/c_fit,h_300,q_35,w_300/");
            return thumbnailImage;
        } else {
            return this.thumbnailImageUrl;
        }

    }


    public String getBetterQualityThumbnailImageUrl(){
        if (mainUrl != null && mainUrl.toLowerCase().contains("upload/")) {
            String thumbnailImage = mainUrl.replace("upload/", "upload/c_fit,h_300,q_auto:good,w_300/");
            return thumbnailImage;
        } else {
            return this.thumbnailImageUrl;
        }

    }

    public void setThumbnailImageUrl(String thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getHashTags() {
        return hashTags;
    }

    public void setHashTags(String hashTags) {
        this.hashTags = hashTags;
    }

    public String getPostCaption() {
        return postCaption;
    }

    public void setPostCaption(String postCaption) {
        this.postCaption = postCaption;
    }

    public String getPostedByUserNodeId() {
        return postedByUserNodeId;
    }

    public void setPostedByUserNodeId(String postedByUserNodeId) {
        this.postedByUserNodeId = postedByUserNodeId;
    }

    public String getPostedByUserName() {
        return postedByUserName;
    }

    public void setPostedByUserName(String postedByUserName) {
        this.postedByUserName = postedByUserName;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getPostsType() {
        return postsType;
    }

    public void setPostsType(String postsType) {
        this.postsType = postsType;
    }

    public String getPostedOn() {
        return postedOn;
    }

    public void setPostedOn(String postedOn) {
        this.postedOn = postedOn;
    }

    public String getPostedByUserEmail() {
        return postedByUserEmail;
    }

    public void setPostedByUserEmail(String postedByUserEmail) {
        this.postedByUserEmail = postedByUserEmail;
    }

    public String getContainerWidth() {
        return containerWidth;
    }

    public void setContainerWidth(String containerWidth) {
        this.containerWidth = containerWidth;
    }

    public String getContainerHeight() {
        return containerHeight;
    }

    public void setContainerHeight(String containerHeight) {
        this.containerHeight = containerHeight;
    }

    public String getPostedByUserFullName() {
        return postedByUserFullName;
    }

    public void setPostedByUserFullName(String postedByUserFullName) {
        this.postedByUserFullName = postedByUserFullName;
    }

    public String getBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(String businessProfile) {
        this.businessProfile = businessProfile;
    }

    public String getHasAudio() {
        return hasAudio;
    }

    public void setHasAudio(String hasAudio) {
        this.hasAudio = hasAudio;
    }

    public String getLikeStatus() {
        return likeStatus;
    }

    public void setLikeStatus(String likeStatus) {
        this.likeStatus = likeStatus;
    }

    public String getTaggedUserCoordinates() {
        return taggedUserCoordinates;
    }

    public void setTaggedUserCoordinates(String taggedUserCoordinates) {
        this.taggedUserCoordinates = taggedUserCoordinates;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getProductUrl() {
        return productUrl;
    }

    public void setProductUrl(String productUrl) {
        this.productUrl = productUrl;
    }

    public String getMinInstallationPrice() {
        return minInstallationPrice;
    }

    public void setMinInstallationPrice(String minInstallationPrice) {
        this.minInstallationPrice = minInstallationPrice;
    }

    public String getMaxInstallationPrice() {
        return maxInstallationPrice;
    }

    public void setMaxInstallationPrice(String maxInstallationPrice) {
        this.maxInstallationPrice = maxInstallationPrice;
    }

    public String getMinWigPrice() {
        return minWigPrice;
    }

    public void setMinWigPrice(String minWigPrice) {
        this.minWigPrice = minWigPrice;
    }

    public String getMaxWigPrice() {
        return maxWigPrice;
    }

    public void setMaxWigPrice(String maxWigPrice) {
        this.maxWigPrice = maxWigPrice;
    }

    public String getSalon() {
        return salon;
    }

    public void setSalon(String salon) {
        this.salon = salon;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductUsed() {
        return productUsed;
    }

    public void setProductUsed(String productUsed) {
        this.productUsed = productUsed;
    }

    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(String totalComments) {
        this.totalComments = totalComments;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getNegotiable() {
        return negotiable;
    }

    public void setNegotiable(String negotiable) {
        this.negotiable = negotiable;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public ArrayList<ExploreLikedByUsersDatas> getLikedByUsers() {
        return likedByUsers;
    }

    public void setLikedByUsers(ArrayList<ExploreLikedByUsersDatas> likedByUsers) {
        this.likedByUsers = likedByUsers;
    }

    public ArrayList<ProductCategoryDatas> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(ArrayList<ProductCategoryDatas> categoryData) {
        this.categoryData = categoryData;
    }

    public String getClickCount() {
        return clickCount;
    }

    public void setClickCount(String clickCount) {
        this.clickCount = clickCount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFollowRequestStatus() {
        return followRequestStatus;
    }

    public void setFollowRequestStatus(String followRequestStatus) {
        this.followRequestStatus = followRequestStatus;
    }

    public String getMemberProfilePicUrl() {
        return memberProfilePicUrl;
    }

    public void setMemberProfilePicUrl(String memberProfilePicUrl) {
        this.memberProfilePicUrl = memberProfilePicUrl;
    }

    public String getMembername() {
        return membername;
    }

    public void setMembername(String membername) {
        this.membername = membername;
    }

    public String getImageUrl1() {
        return imageUrl1;
    }

    public void setImageUrl1(String imageUrl1) {
        this.imageUrl1 = imageUrl1;
    }

    public String getThumbnailUrl1() {
        return thumbnailUrl1;
    }

    public void setThumbnailUrl1(String thumbnailUrl1) {
        this.thumbnailUrl1 = thumbnailUrl1;
    }

    public String getContainerHeight1() {
        return containerHeight1;
    }

    public void setContainerHeight1(String containerHeight1) {
        this.containerHeight1 = containerHeight1;
    }

    public String getContainerWidth1() {
        return containerWidth1;
    }

    public void setContainerWidth1(String containerWidth1) {
        this.containerWidth1 = containerWidth1;
    }

    public String getImageUrl2() {
        return imageUrl2;
    }

    public void setImageUrl2(String imageUrl2) {
        this.imageUrl2 = imageUrl2;
    }

    public String getThumbnailUrl2() {
        return thumbnailUrl2;
    }

    public void setThumbnailUrl2(String thumbnailUrl2) {
        this.thumbnailUrl2 = thumbnailUrl2;
    }

    public String getContainerHeight2() {
        return containerHeight2;
    }

    public void setContainerHeight2(String containerHeight2) {
        this.containerHeight2 = containerHeight2;
    }

    public String getContainerWidth2() {
        return containerWidth2;
    }

    public void setContainerWidth2(String containerWidth2) {
        this.containerWidth2 = containerWidth2;
    }

    public String getImageUrl3() {
        return imageUrl3;
    }

    public void setImageUrl3(String imageUrl3) {
        this.imageUrl3 = imageUrl3;
    }

    public String getThumbnailUrl3() {
        return thumbnailUrl3;
    }

    public void setThumbnailUrl3(String thumbnailUrl3) {
        this.thumbnailUrl3 = thumbnailUrl3;
    }

    public String getContainerHeight3() {
        return containerHeight3;
    }

    public void setContainerHeight3(String containerHeight3) {
        this.containerHeight3 = containerHeight3;
    }

    public String getContainerWidth3() {
        return containerWidth3;
    }

    public void setContainerWidth3(String containerWidth3) {
        this.containerWidth3 = containerWidth3;
    }

    public String getThumbnailUrl4() {
        return thumbnailUrl4;
    }

    public void setThumbnailUrl4(String thumbnailUrl4) {
        this.thumbnailUrl4 = thumbnailUrl4;
    }

    public String getImageUrl4() {
        return imageUrl4;
    }

    public void setImageUrl4(String imageUrl4) {
        this.imageUrl4 = imageUrl4;
    }

    public String getContainerHeight4() {
        return containerHeight4;
    }

    public void setContainerHeight4(String containerHeight4) {
        this.containerHeight4 = containerHeight4;
    }

    public String getContainerWidth4() {
        return containerWidth4;
    }

    public void setContainerWidth4(String containerWidth4) {
        this.containerWidth4 = containerWidth4;
    }

    public String getIsPromoted() {
        return isPromoted;
    }

    public void setIsPromoted(String isPromoted) {
        this.isPromoted = isPromoted;
    }

    public boolean isToRemoveHomeItem() {
        return isToRemoveHomeItem;
    }

    public void setToRemoveHomeItem(boolean toRemoveHomeItem) {
        isToRemoveHomeItem = toRemoveHomeItem;
    }
}
