//index.js
//获取应用实例
const app = getApp()
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({
  data: {
    motto: '我看你的认真，小伙子',
    userInfo: {},
    userData: '',
    hasUserInfo: false,
    canIUse: wx.canIUse('button.open-type.getUserInfo'),
    userId: '',
  },
  //事件处理函数
  bindViewTap: function () {
    wx.navigateTo({
      url: '../user/userDetails',
    })
  },

  /**
   * 设置
   */
  setUp:function(e){
    if (this.data.userId == null || app.globalData.userId == null) {
      wx.showModal({
        title: '登录提示',
        content: '请在当前页面【点击登录】',
        showCancel: false,
      })
    }
    else {
      wx.navigateTo({
        url: '../userSetUp/userSetUp',
      })
    }
    
  },

  /**
   * 查看我的收藏
   */
  getCollections:function(e) {
    if (this.data.userId == null || app.globalData.userId == null) {
      wx.showModal({
        title: '登录提示',
        content: '请在当前页面【点击登录】',
        showCancel:false,
      })
    }
    else {
      wx.navigateTo({
        url: '../collections/collections',
      })
    }
  },

  getUserDetails: function (e) {
    //var data = { "openId": app.globalData.openId};  
    wx.request({
      url: app.globalData.host +'/userInfo/getUserDetails?userId=' + this.data.userId, //仅为示例，并非真实的接口地址
      method: 'GET',
      //data: JSON.stringify(data),
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          wx.navigateTo({
            url: '../editUser/editUser?data=' + JSON.stringify(res.data.data),    //?data=' + JSON.stringify(res.data),
          })
        } else {
          console.log("获取用户信息失败");
          wx.showToast({
            title: '获取数据失败',
            icon: 'none',
          })
        }
      }
    })
  },

  getAllMyList: function (e) { 
    if (this.data.userId == null || app.globalData.userId == null) {
      wx.showModal({
        title: '登录提示',
        content: '请在当前页面【点击登录】',
        showCancel: false,
      })
    }
    else {
      var that = this
      wx.navigateTo({
        url: '../needsManager/needsManager?userId='+that.data.userId+'&whichOperation='+ JSON.stringify(e.currentTarget.dataset.whichoperation),
      })
    }
  },

  /**
   * 添加需求
   */
  addNew: function (e) {
    if (this.data.userId == null || app.globalData.userId == null) {
      wx.showModal({
        title: '登录提示',
        content: '请在当前页面【点击登录】',
        showCancel: false,
      })
    }
    else {
      wx.navigateTo({
        url: '../addNew/addNew'
      })
    }
  },

  
  onLoad: function () {
    this.getUserInfoFromApp()
  },

  onShow:function() {
    var that = this
    that.checkSession()
  },

  /**
   * 检查用户是否登录过期
   */
  checkSession : function(){
    var that = this
    wx.checkSession({
      success: function (res) {
        console.log(res, '登录未过期')
      },
      fail: function (res) {
        //设置系统userId为空
        app.globalData.userId = null
        console.log(res, '登录过期了')
        wx.showModal({
          title: '过期登录',
          content: '请在当前页面【点击登录】',
          showCancel: false
        })
      }
    })
  },

  /**
   * 从app页面获取用户数据
   */
  getUserInfoFromApp:function(e) {
    let that = this;
    that.setData({
      userId: app.globalData.userId,
      userInfo: app.globalData.userInfo,
    })
    console.log("userPage userId:"+that.data.userId)
  },

  getUserInfo: function (e) {
    console.log(e)
    app.globalData.userInfo = e.detail.userInfo
    this.setData({
      userInfo: e.detail.userInfo,
      hasUserInfo: true
    })
  },

  /**
   * 登录
   */
  signIn: function (e) {
    let that = this;
    wx.redirectTo({
      url:'../index/index'
    })
    //that.login()
  },

  /**
   * 登录接口
   */
  login: function () {
    var that = this
    wx.showModal({
      title: '授权登录',
      content: '确定授权登录吗？',
      confirmText: "授权",
      icon: 'success',
      duration: 1000,
      success: function (res) {
        if (res.confirm) {
          that.getUserInfoFromBack()
        }
      }
    })
  },

  /**
   * 通过后台传递code获取用户信息
   */
  getUserInfoFromBack: function () {
    var that = this
    // 登录
    wx.login({
      success: function (res) {
        var code = res.code;//登录凭证
        if (code) {
          //2、调用获取用户信息接口
          wx.getUserInfo({
            success: function (res) {
              console.log({ encryptedData: res.encryptedData, iv: res.iv, code: code })

              // 可以将 res 发送给后台解码出 unionId
              app.globalData.userInfo = res.userInfo
              console.log("登录获取到的用户信息：" + app.globalData.userInfo.nickName)
              that.setData({
                userInfo: app.globalData.userInfo,
              })

              //从后台获取微信官方用户数据
              that.talkWithBack(res, code)
            },
            fail: function () {
              console.log("系统出错")
              wx.showLoading({
                title: '',
                mask: true,
                success: function (res) { },
                fail: function (res) { },
                complete: function (res) { },
              })
            }
          })
        } else {
          console.log('获取用户登录态失败！' + r.errMsg)
        }
      },
      fail: function () {
        wx.showToast({
          title: '与服务器失联啦',
          icon: 'none'
        })
      }
    })
  },

  /**
   * 后台接口
   */
  talkWithBack: function (res, code) {
    var that = this
    console.log("res信息:" + { encryptedData: res.encryptedData, iv: res.iv, code: code })
    //3.请求自己的服务器，解密用户信息 获取unionId等加密信息
    wx.request({
      url: app.globalData.host +'/userInfo/IfAuthorizationEd',
      method: 'get',
      header: {
        "Content-Type": "applciation/json"
      },
      data: { encryptedData: res.encryptedData, iv: res.iv, code: code },
      success: function (data) {
        console.log(data);
        //4.解密成功后 获取自己服务器返回的结果
        if (data.data.isSuccess) {
          console.log("第一次从后台获取到用户数据，用户昵称：" + data.data.data)
          app.globalData.openid = data.data.data.openid

          console.log("测试登录userInfo:" + app.globalData.userInfo.nickName)
          wx.request({
            url: app.globalData.host +'/userInfo/addUserInfoIfNoSaved',//自己的服务接口地址
            method: 'POST',
            header: {
              'content-type': 'application/json' // 默认值
            },
            data: JSON.stringify({
              "openId": app.globalData.openid, "nickName": app.globalData.userInfo.nickName, "image": app.globalData.userInfo.avatarUrl, "headUrl": app.globalData.userInfo.country + '' + app.globalData.userInfo.province + '' + app.globalData.userInfo.city, "sex": app.globalData.userInfo.gender
            }),
            success: function (res) {
              if (res.data.isSuccess) {
                console.log("第二次请求后台获取用户数据，用户id:" + res.data.data)
                console.log("用户数据保存或者查询成功,userId:" + res.data.data)
                app.globalData.userId = res.data.data;
                that.setData({
                  userId: res.data.data,
                })
                app.globalData.token = 'XyXyRuiDer_userId_' + res.data.data
              }
              else {
                console.log("用户数据保存或者查询失败:" + res.data.message)
                wx.showToast({
                  title: '登录失败',
                  icon: 'none',
                  duration: 500,
                })
              }
            },
          })
        } else {
          console.log('解密失败')
          wx.showToast({
            title: '登录失败',
            icon: 'none',
            duration: 500,
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

})
