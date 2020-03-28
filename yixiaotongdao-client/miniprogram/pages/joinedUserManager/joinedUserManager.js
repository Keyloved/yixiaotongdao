// miniprogram/pages/joinedUserManager/joinedUserManager.js
const app = getApp();
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    needsId: '',
    userId: '',
    joinUserInfoList:'',
  },

  /**
   * 剔除参与组队人员
   */
  eliminate:function(e) {
    //检查用户登录是否过期
    //utils.checksession();

    var that = this
    console.log("joinedUserid:" + e.currentTarget.dataset.joineduserid)
    var joinedUserId = e.currentTarget.dataset.joineduserid
    wx.showModal({
      title: '',
      content: '确定剔除吗？',
      success: function (res) {
        if (res.confirm) {
          console.log("needsId:" + that.data.needsId);
          wx.request({
            url:app.globalData.host +'/needsManagement/eliminateJoinUser?needId=' + that.data.needsId + '&joinedUserId=' + joinedUserId,
            method: 'GET',
            header: {
              'content-type': 'application/json', // 默认值
              'token': app.globalData.token
            },
            success: (res) => {
              if (res.data.isSuccess) {
                wx.showToast({
                  title: '剔除成功',
                  icon: 'success'
                })
              }
              else {
                wx.showToast({
                  title: '剔除失败',
                  icon: 'none'
                })
              }
            }
          })

          //加载需求组队人员详情
          that.getNeedAndJoinUsers();
        }
      }
    })
  },

  /**
   * 剔除特定队伍人员
   */
  eliminateJoinUser:function(joinedUserId) {
    //检查用户登录是否过期
    //utils.checksession();

    var that = this;
    console.log("needsId:" + this.data.needsId);
    wx.request({
      url:app.globalData.host +'/needsManagement/eliminateJoinUser?needId=' + this.data.needsId + '&joinedUserId=' + joinedUserId,
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          var item = res.data.data
          console.log("item" + item)

          that.setData({
            joinUserInfoList: item.joinUsersInfo,
          })
        }
        else {
          wx.showToast({
            title: '获取详情失败',
            icon: 'none'
          })
        }
      }
    })
  },

  /**
  * 进入用户详情页面
  */
  enterUserInfo: function (e) {
    var that = this;
    console.log("userid:" + e.currentTarget.dataset.userid)
    wx.navigateTo({
      url: '../userInfo/userInfo?senderId=' + JSON.stringify(e.currentTarget.dataset.userid),
    })
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this
    var needsId = JSON.parse(options.needsId)
    console.log("needsId:" + needsId)
    that.setData({
      needsId: needsId,
      userId:app.globalData.userId
    })
    //加载需求组队人员详情
    that.getNeedAndJoinUsers();
  },

  /**
     * 获取需求详情以及需求组队队员信息
     */
  getNeedAndJoinUsers: function (e) {
    var that = this;
    console.log("needsId:" + this.data.needsId);
    wx.request({
      url:app.globalData.host +'/needsManagement/getNeedAndJoinUsers?needId=' + this.data.needsId,
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          var item = res.data.data
          console.log("item" + item)

          that.setData({
            joinUserInfoList: item.joinUsersInfo,
          })
        }
        else {
          wx.showToast({
            title: '获取详情失败',
            icon: 'none'
          })
        }
      }
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {

  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    //加载需求组队人员详情
    this.getNeedAndJoinUsers();
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {

  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {

  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {

  },

  /**
   * 页面上拉触底事件的处理函数
   */
  onReachBottom: function () {

  },

  /**
   * 用户点击右上角分享
   */
  onShareAppMessage: function () {

  }
})