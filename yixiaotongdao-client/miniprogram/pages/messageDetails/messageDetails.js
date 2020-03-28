// miniprogram/pages/applyMessageList/applyMessageList.js
const app = getApp();
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
    userId: null,
    messageList: {},
    categoryId: '',
    isReply: '',
    isApproved:null,
    intervarID:'',
  },

  /**
   * 进入用户详情
   */
  enterUserInfo:function(e){
    var that = this;
      wx.navigateTo({
        url: '../userInfo/userInfo?senderId=' + JSON.stringify(e.currentTarget.dataset.senderid),
      })
  },

  /**
   * 进入需求详情页面
   */
  enterNeedAndMessageDetails:function(e) {
    console.log(e.currentTarget.dataset.item)
    wx.navigateTo({
      url: '../needsDetails/needsDetails?data=' + JSON.stringify(e.currentTarget.dataset.item),
    })
  },

  /**
   * 同意或者拒绝操作
   */
  approveOrRefuse: function (e) {
    //检查用户登录是否过期
    //utils.checksession();

    var that = this
    var isApproved = e.currentTarget.dataset.isapproved
    var approveStr = (isApproved == 2?"同意":"拒绝")
    wx.showModal({
      title: '',
      content: '确定' + approveStr + "吗?",
      success:function(res) {
        if (res.confirm) {
          that.approveOrRefuseToBack(e)
        }
      }
    })
    
  },

  /**
   * 同意或拒绝请求后台操作
   */
  approveOrRefuseToBack:function(e) {
    var that = this
    var item = e.currentTarget.dataset.item;
    console.log("item:" + item);
    wx.request({
      url: app.globalData.host +'/MessageManage/setIsApproved',
      data: { "messageId": item.id, "needsId": item.needsId, "senderId": item.recipientId, "recipientId": item.senderId, "isApproved": e.currentTarget.dataset.isapproved, "content": item.messageContent },
      method: 'POST',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          wx.showToast({
            title: res.data.message,
            icon: 'success'
          })
        }
        else {
          wx.showToast({
            title: '操作失败',
            icon: 'none'
          })
        }
      },
      fail: function (res) {
        wx.showToast({
          title: '与服务器失联啦',
          icon: 'none',
          duration: 1000,
        })
      }
    })
  },

  /**
   * 回复操作
   */
  reply: function (e) {
    console.log(e.currentTarget.dataset.item),
      wx.navigateTo({
        url: '../evaluatePage/evaluate?data=' + JSON.stringify(e.currentTarget.dataset.item) + '&categoryId=' + JSON.stringify(e.currentTarget.dataset.categoryid),
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
    var categoryId = JSON.parse(options.categoryId);
    var isReply = JSON.parse(options.isReply);
    that.setData({
      categoryId: categoryId,
      isReply: isReply
    })
    
  },

  /**
   * 获取消息列表
   */
  getMessageList:function(e) {
    var that = this
    wx.request({
      url:app.globalData.host +'/MessageManage/getMessageList?userId=' + that.data.userId + '&categoryId=' + that.data.categoryId + '&isReply=' + that.data.isReply,
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          var array = res.data.data;
          console.log("list大小:" + array.length)
          console.log("array是"+array);
          for (let i = 0, len = array.length; i < len; i += 1) {
            //array[i].deadline = array[i].deadline.substring(0, 10);
            //array[i].startTime = array[i].startTime.substring(0, 10);
          }
          that.setData({
            messageList: array,
          })
          console.log("messageList是"+that.data.messageList);
        }
        else {
          wx.showToast({
            title:'获取失败',
            icon:'none'
          })
        }
      },
      fail: function (res) {
        wx.showToast({
          title: '与服务器失联啦',
          icon: 'none'
        })
      }
    })
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    var that = this
    that.getMessageList();
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    var that = this
    //当用户登录时，每一秒刷新一次
    if (that.data.userId != null || app.globalData.userId != null) {
      //每1秒刷新一次页面
      that.data.intervarID = setInterval(function () {
        that.getMessageList();
      }, 1000)
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    var that = this
    //清除计时器  即清除intervarID
    clearInterval(that.data.intervarID)
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
    var that = this
    //清除计时器  即清除intervarID
    clearInterval(that.data.intervarID)
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    let that = this;
    wx.showNavigationBarLoading()
    setTimeout(function () {
      //加载消息列表
      that.getMessageList();
      wx.hideNavigationBarLoading()
      wx.stopPullDownRefresh()
    }, 1500);
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