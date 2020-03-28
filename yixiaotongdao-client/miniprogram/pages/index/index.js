// miniprogram/pages/index/index.js
const app = getApp()
Page({

  /**
   * 页面的初始数据
   */
  data: {
    hasUserInfo:false,
    canIUse:false,
    // 判断小程序的API，回调，参数，组件等是否在当前版本可用。
    canIUse: wx.canIUse('button.open-type.getUserInfo')
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
    var that = this;
    // 查看是否授权
    wx.getSetting({
      success: function (res) {
        if (res.authSetting['scope.userInfo']) {
          wx.getUserInfo({
            success: function (res) {
              //从数据库获取用户信息
              app.globalData.userInfo = res.userInfo
              that.talkWithBack()
              //用户已经授权过
              wx.switchTab({
                url: '/pages/main/main'
              })
            }
          });
        }
      }
    })
  },

  /**
   * 获取用户数据
   */
  getUserInfos: function (e) {
    console.log("e.headUrl:" + e.detail.userInfo.country)
    if (e.detail.userInfo) {
      //用户按了允许授权按钮
      var that = this;
      app.globalData.userInfo = e.detail.userInfo
      //插入登录的用户的相关信息到数据库
      that.talkWithBack()
      //授权成功后，跳转进入小程序首页
      wx.switchTab({
        url: '/pages/main/main'
      })
    } 
  },

  //获取用户信息接口
  queryUsreInfo: function () {
    wx.request({
      url: app.globalData.host + 'userInfo/getUserInfoByOpenId',
      data: {
        openid: app.globalData.openid
      },
      header: {
        'content-type': 'application/json'
      },
      success: function (res) {
        console.log(res.data.data);
        app.globalData.userInfo = res.data.data;
        app.globalData.userId = res.data.data.id
        app.globalData.token = 'XyXyRuiDer_userId_' + res.data.data.id
      }
    })
  },

  /**
   * 后台接口
   */
  talkWithBack: function (e) {
    var that = this
    console.log("headUrl:" + app.globalData.userInfo.country),
    wx.request({
      url: app.globalData.host + '/userInfo/addUserInfoIfNoSaved',
      method: 'POST',
      header: {
        'content-type': 'application/json' // 默认值
      },

      data: JSON.stringify({
        "openId": app.globalData.openid, "nickName": app.globalData.userInfo.nickName, "image": app.globalData.userInfo.avatarUrl, "headUrl": app.globalData.userInfo.country, "sex": app.globalData.userInfo.gender
      }),
      success: function (res) {
        if (res.data.isSuccess) {
          console.log("第二次请求后台获取用户数据，用户id:" + res.data.data)
          console.log("用户数据保存或者查询成功,userId:" + res.data.data)
          app.globalData.userId = res.data.data;
          app.globalData.token = 'XyXyRuiDer_userId_' + res.data.data
          wx.switchTab({
            url: '/pages/main/main'
          })
        }
        else {
          console.log("用户数据保存或者查询失败:" + res.data.message)
          /**wx.showToast({
            title: '登录失败',
            icon: 'none',
            duration: 500,
          })
          */
          wx.switchTab({
            url: '/pages/main/main'
          })
        }
        
      },
      fail: function () {
        console.log("系统出错")
        wx.showToast({
          title: '系统出错',
          icon: 'none'
        })
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