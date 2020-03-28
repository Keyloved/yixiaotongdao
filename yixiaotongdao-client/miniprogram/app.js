//app.js
App({
  globalData: {
    hasUserInfo: false,
    canIUse: false,
    userInfo: null,
    openId: null,
    userId:null,
    token:null,
    show:false,
    host:'https://test.zwding.ac.cn:8443',
    //host:'http://localhost:8443',
    //host:'www.yixiaotongdao.cn'
  },

 

  onLaunch: function () {
    this.checkUserLogined()
  },

  /**
   * 登录接口
   */
  checkUserLogined:function() {
    var that = this
    // 登录
    wx.login({
      success: function (res) {
        var code = res.code;
        if (code) {
          wx.request({
            url: that.globalData.host +'/userInfo/weixin/session?code='+code,//自己的服务接口地址
            method: 'GET',
            header: {
              'content-type': 'application/json' // 默认值
            },
            success: function (res) {
              if (res.data.isSuccess) {
                console.log("用户数据保存或者查询成功,user:" + res.data.data.user)
                var user = res.data.data.user
                var openid = res.data.data.openid
                //console.log("user 长度:"+user.length)
                that.globalData.openid = openid
                if (user==null){
                  //2、调用获取用户信息接口
                  /*wx.getUserInfo({
                    success: function (res) {
                      console.log({ encryptedData: res.encryptedData, iv: res.iv, code: code })

                      // 可以将 res 发送给后台解码出 unionId
                      that.globalData.userInfo = res.userInfo
                      console.log("登录获取到的用户信息：" + that.globalData.userInfo.nickName)
                      //从后台获取微信官方用户数据
                      that.login()
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
                  }) */
                }
                else {
                  that.globalData.hasUserInfo = true
                  that.globalData.canIUse = true
                  //2、调用获取用户信息接口
                  /*wx.getUserInfo({
                    success: function (res) {
                      console.log({ encryptedData: res.encryptedData, iv: res.iv, code: code })

                      // 可以将 res 发送给后台解码出 unionId
                      that.globalData.userInfo = res.userInfo
                      console.log("登录获取到的用户信息：" + that.globalData.userInfo.nickName)

                      //从后台获取微信官方用户数据
                      that.talkWithBack()
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
                  })*/
                }
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
        }
      }
    })
  },

  //用户首次登录
  login:function(e) {
    var that = this
    wx.showModal({
      title: '授权登录',
      content: '确定授权登录吗？',
      icon: 'success',
      confirmText: "授权",
      duration: 1000,
      success: function (res) {
        if (res.confirm) {
          that.talkWithBack()
        }
      }
    })
  },


  /**
   * 后台接口
   */
  talkWithBack: function (res, code) {
    var that = this
    wx.request({
      url: that.globalData.host +'/userInfo/addUserInfoIfNoSaved',//自己的服务接口地址
      method: 'POST',
      header: {
        'content-type': 'application/json' // 默认值
      },
      data: JSON.stringify({
        "openId": that.globalData.openid, "nickName": that.globalData.userInfo.nickName, "image": that.globalData.userInfo.avatarUrl, "headUrl": that.globalData.userInfo.country + '' + that.globalData.userInfo.province + "" + that.globalData.userInfo.city, "sex": that.globalData.userInfo.gender
      }),
      success: function (res) {
        if (res.data.isSuccess) {
          console.log("第二次请求后台获取用户数据，用户id:" + res.data.data)
          console.log("用户数据保存或者查询成功,userId:" + res.data.data)
          that.globalData.userId = res.data.data;
          that.globalData.token = 'XyXyRuiDer_userId_' + res.data.data
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
   * 提供给userPage调用，关键的函数，禁止随意更改
   */
  getUserInfo1: function () {
    var that = this;
    var ruider = this
    // 展示本地存储能力
    var logs = wx.getStorageSync('logs') || []
    logs.unshift(Date.now())
    wx.setStorageSync('logs', logs)
    // 登录
    wx.login({
      success: function (res) {
        var code = res.code;//登录凭证
        if (code) {
          //2、调用获取用户信息接口
          wx.getUserInfo({
            success: function (res) {
              console.log({ encryptedData: res.encryptedData, iv: res.iv, code: code })
              //3.请求自己的服务器，解密用户信息 获取unionId等加密信息
              wx.request({
                url: that.globalData.host +'/userInfo/IfAuthorizationEd',
                method: 'get',
                header: {
                  "Content-Type": "applciation/json"
                },
                data: { encryptedData: res.encryptedData, iv: res.iv, code: code },
                success: function (data) {
                  console.log(data);
                  //4.解密成功后 获取自己服务器返回的结果
                  if (data.data.isSuccess) {
                    console.log(that.globalData.userInfo.nickName)
                    wx.showModal({
                      title: '授权登录',
                      content: '确定授权登录吗？',
                      icon: 'success',
                      confirmText: "授权",
                      duration: 2000,
                      success: function (res) {
                        //this.globalData.openId = data.data.data.openid
                        if (res.confirm) {
                          that.globalData.openId = data.data.data.openid;
                          wx.request({
                            url: that.globalData.host +'/userInfo/addUserInfoIfNoSaved',//自己的服务接口地址
                            method: 'POST',
                            header: {
                              'content-type': 'application/json' // 默认值
                            },
                            data: JSON.stringify({
                              "openId": data.data.data.openid, "nickName": that.globalData.userInfo.nickName, "image": that.globalData.userInfo.avatarUrl, "headUrl": that.globalData.userInfo.country + '+_+' + that.globalData.userInfo.province + '+_+' + that.globalData.userInfo.city, "sex": that.globalData.userInfo.gender
                            }),
                            success: function (res) {
                              if (res.data.isSuccess) {
                                console.log("获取用户数据:" + res)
                                console.log("用户数据保存或者查询成功,userId:" + res.data.data)
                                that.globalData.userId = res.data.data;
                                that.globalData.token = 'XyXyRuiDer_userId_' + res.data.data
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
                            fail: function () {
                              wx.showToast({
                                title: '登录失败',
                                icon: 'none',
                                duration: 500,
                              })
                            }
                          })
                        }
                      }
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
    // 获取用户信息
    wx.getSetting({
      success: res => {
        if (res.authSetting['scope.userInfo']) {
          // 已经授权，可以直接调用 getUserInfo 获取头像昵称，不会弹框
          wx.getUserInfo({
            success: res => {
              // 可以将 res 发送给后台解码出 unionId
              this.globalData.userInfo = res.userInfo
              // 由于 getUserInfo 是网络请求，可能会在 Page.onLoad 之后才返回
              // 所以此处加入 callback 以防止这种情况
              if (this.userInfoReadyCallback) {
                this.userInfoReadyCallback(res)
              }
            }
          })
        }
      }
    })
    return this.globalData.userInfo
  }

  

})