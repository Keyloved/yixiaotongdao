// miniprogram/pages/evaluatePage/evaluate.js
const app = getApp();
var utils = require('/../../utils/utils.js');
// 在需要使用的js文件中，导入js
//var utils = require('/../../utils/checkSession.js');
Page({
  /**
   * 页面的初始数据
   */
  data: {
    current: 0,
    userId: null,
    recipientId: '',
    whichOperation: '',
    year:0,
    month:0,
    day:0,
    time:''
  },

  /**
   * 监听日期picker选择器
   */
  listenerTimePickerSelected: function (e) {
    this.setData({
      time: e.detail.value
    })
  },

  /**
   * 检测输入是否为数字
   */
  checkIsNum:function(e){
    var that = this
    //判断用户输入的是否为数字
    var regNum = new RegExp('[0-9]', 'g');
    //测试数据，不为数字则返回null
    var rsNum = regNum.exec(e.detail.value);
    if (rsNum == null) {
      that.setData({
        year:'',
        month:'',
        day:''
      })
      wx.showToast({
        title: '请输入数字',
        icon:'none',
        duration:500
      })
    }
  },


  limit: function (e) {
    var value = e.detail.value;
    var length = parseInt(value.length);

    if (length > this.data.noteMaxLen) {
      return;
    }

    this.setData({
      current: length
    });
  },

  formSubmit: function (e) {
    //检查用户登录是否过期
    //utils.checksession();

    var that = this
    
    if (that.data.whichOperation == 'writeToFuture' && e.detail.value.receiverMail.length == 0) {
      wx.showToast({
        title: '邮箱不能为空',
        icon: 'none',
      })
    }
    else if (e.detail.value.content.length == 0) {
      wx.showToast({
        title: '内容不能为空',
        icon: 'none',
      })
    }
    else {
      wx.showModal({
        title: '',
        content: '确定发表吗？',
        icon: 'success',
        duration: 2000,
        success: function (res) {
          if (res.confirm) {
            //说毕业
            if (that.data.whichOperation == 'sayGraduation') {   
              that.addLeaveingManage(e,5, "")
            }
            //表白墙
            else if (that.data.whichOperation == 'confessionWall') { 
              that.addLeaveingManage(e,6, "")
            }
            //吐槽
            else if (that.data.whichOperation == 'complaints') {
              that.addLeaveingManage(e, 8, "")
            }
            //写给未来的ta
            else if (that.data.whichOperation == 'writeToFuture') {
              that.writeToFuture(e, 7)
            }

          }
        }
      })

    }
  },

  /**
   * 写给未来的ta
   */
  writeToFuture:function(e, categoryId) {
    var url = app.globalData.host +'/leaveingManage/writeToFuture'
    var data = { "categoryId": categoryId, "userId": app.globalData.userId, "content": e.detail.value.content, "mark": e.detail.value.receiverMail, "future": e.detail.value.time };
    wx.request({
      url: url,
      method: 'POST',
      data: JSON.stringify(data),
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          wx.showToast({
            title: '发表成功',
            icon: 'success',
            duration: 500,
          })
          console.log("发表成功");
        }
        else {
          wx.showToast({
            title: '发表失败',
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
   * 添加留言
   */
  addLeaveingManage: function (e,categoryId, mark) {
    var url = app.globalData.host +'/leaveingManage/addLeaveingManage'
    var data = { "categoryId": categoryId, "userId": app.globalData.userId, "content": e.detail.value.content, "mark": mark };
    wx.request({
      url: url,
      method: 'POST',
      data: JSON.stringify(data),
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          wx.showToast({
            title: '发表成功',
            icon: 'success',
            duration: 500,
          })
          console.log("发表成功");
        }
        else {
          wx.showToast({
            title: '发表失败',
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
    that.setData({
      time: utils.formatTime(new Date())
    })
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