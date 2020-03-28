// miniprogram/pages/evaluatePage/evaluate.js
const app = getApp();
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({
  /**
   * 页面的初始数据
   */
  data: {
    current: 0,
    userId: app.globalData.userId,
    recipientId:'',
    messageId:'',
    categoryId:'',
    item:{},
    whichOperation:'',
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
    if (e.detail.value.content.length == 0){
      wx.showToast({
        title: '内容不能为空',
        icon: 'fail',
      })
    }
    else{
      if(this.data.item.needsId==null){   //评论或者申请理由
        //添加评论或者申请加入/退出
        that.addApplyOrEvaluate(e)
      }
      else {   //回复
        wx.showModal({
          title: '',
          content: '确定发表吗？',
          icon: 'success',
          duration: 2000,
          success: function (res) {
            if (res.confirm) {
              //添加回复
              that.addReply(e)
            }
          }
          })
        }
    }
  },

  /**
   * 添加评论或者申请加入/退出
   */
  addApplyOrEvaluate:function(e) {
    //默认请求url为评论/回复
    var url =app.globalData.host +'/MessageManage/addEvaluate'
    if (this.data.categoryId == 1) {
      //如果是申请加入/退出，改变请求url为addApply
      url =app.globalData.host +'/MessageManage/addApply'
    }
    var data = { "categoryId": this.data.categoryId, "senderId": this.data.userId, "content": e.detail.value.content, "needsId": this.data.item.id, "recipientId": this.data.recipientId, "messageId": this.data.messageId };
    wx.showModal({
      title: '',
      content: '确定发表吗？',
      icon: 'success',
      duration: 2000,
      success: function (res) {
        if (res.confirm) {
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
                wx.showModal({
                  title: 'success',
                  content: '操作成功',
                  icon: 'success',
                  showCancel: false,
                  success: function (res) {
                    if (res.confirm) {
                      //返回上一页
                      wx.navigateBack({
                        delta: -1
                      })
                    }
                  }
                })
                console.log("新增评论信息成功");
              }
              else {
                wx.showModal({
                  title: 'fail',
                  content: '操作失败',
                  icon: 'none',
                  showCancel: false,
                  success: function (res) {
                    if (res.confirm) {
                      //返回上一页
                      wx.navigateBack({
                        delta: -1
                      })
                    }
                  }
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
        }
      }
    })
  },

  /**
   * 添加回复,包括回复评论和回复回复两种
   */
  addReply:function(e) {
    //判断回复来自用户回复的是评论还是回复回复，回复评论的话，item.messageId为0（数据库默认评论消息messageId为0），回复回复的话，messageId不为空
    var messageId = this.data.item.messageId
    console.log("messageId是：" + messageId)
    if (messageId == 0) {
      messageId = this.data.item.id
    }

    var data = { "categoryId": this.data.categoryId, "senderId": this.data.userId, "content": e.detail.value.content, "needsId": this.data.item.needsId, "messageId": messageId, "recipientId": this.data.item.senderId };
    wx.request({
      url:app.globalData.host +'/MessageManage/addEvaluate',
      method: 'POST',
      data: JSON.stringify(data),
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        if (res.data.isSuccess) {
          wx.showModal({
            title: 'success',
            content: '回复成功',
            icon: 'success',
            showCancel: false,
            success: function (res) {
              if (res.confirm) {
                //返回上一页
                wx.navigateBack({
                  delta: -1
                })
              }
            }
          })
          console.log("新增回复信息成功");
        }
        else {
          wx.showModal({
            title: 'fail',
            content: '回复失败',
            icon: 'none',
            showCancel: false,
            success: function (res) {
              if (res.confirm) {
                //返回上一页
                wx.navigateBack({
                  delta: -1
                })
              }
            }
          })
        }
      },
      fail: (res) => {
        wx.showToast({
          title: '与服务器失联啦',
          icon: 'none',
          duration: 1000,
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
    var item = JSON.parse(options.data);
    var categoryId = JSON.parse(options.categoryId);
    console.log("item" + item);
    console.log("categoryId"+categoryId);
    console.log("whichOperation"+whichOperation);
    if (item.recipientId != null) {
      that.setData({
        whichOperation: whichOperation,
        item: item,
        categoryId: categoryId,
        recipientId: item.senderId,
      })
    }
    else {
      that.setData({
        whichOperation: whichOperation,
        item: item,
        categoryId: categoryId,
        recipientId: item.userId,
      })
    }
    

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