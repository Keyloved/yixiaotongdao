// miniprogram/pages/evaluatePage/evaluate.js
const app = getApp();
// 在需要使用的js文件中，导入js
//var utils = require('/../../utils/checkSession.js');
Page({
  /**
   * 页面的初始数据
   */
  data: {
    leaveingManageList:{},
    userId: null,
    whichOperation: '',
  },

  /**
   * 进入用户详情
   */
  enterUserInfo: function (e) {
    var that = this;
    wx.navigateTo({
      url: '../../userInfo/userInfo?senderId=' + JSON.stringify(e.currentTarget.dataset.senderid),
    })
  },

  
  /**
   * 获取所有留言
   */
  getAllLeaveingManage: function (e) {
    var that = this
    var categoryId = 0
    if (that.data.whichOperation == 'sayGraduation') {
      categoryId = 5
    }
    else if (that.data.whichOperation == 'confessionWall') {
      categoryId = 6
    }
    else if (that.data.whichOperation == 'complaints'){
      categoryId = 8
    }
    var url = app.globalData.host +'/leaveingManage/getAllLeaveingManage?categoryId=' + categoryId
    wx.request({
      url: url,
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          console.log("获取所有留言成功");
          console.log("所有留言:"+res.data.data)
          that.setData({
            leaveingManageList: res.data.data
          })
        }
        else {
          wx.showToast({
            title: '获取所有留言失败',
            icon: 'none',
            duration: 500
          })
        }
      },
      fail: function (res) {
        wx.showToast({
          title: '与服务器失联啦',
          icon: 'none',
          duration: 1000
        })
      }
    })
  },


  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    let that = this;
    that.setData({
      userId: app.globalData.userId,
    })
    var whichOperation = JSON.parse(options.whichOperation)
    console.log("whichOperation" + whichOperation);
    that.setData({
      whichOperation: whichOperation,
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
    var that = this
    //获取所有留言
    that.getAllLeaveingManage()
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