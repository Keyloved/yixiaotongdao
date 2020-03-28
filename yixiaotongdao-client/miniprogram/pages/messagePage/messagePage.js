// miniprogram/pages/messagePage/messagePage.js
const app = getApp();
// 在需要使用的js文件中，导入js
//var utils = require('/../utils/checkSession.js');
Page({

  /**
   * 页面的初始数据
   */
  data: {
      graduationSession:'',
      fullTime_day: '',
      fullTime_hou: '',
      fullTime_min: '',
      fullTime_sec: '',
      day: '',
      hou: '',
      min: '',
      sec: '',
      fullDay:'',
      allNoWatchedNo:0,
      applyNo:0,
      evaluateNo:0,
      overTimeNo:0,
      overNumberNo:0,
      eliminateNo:0,
      userId: '',
      intervarID:'',
      clock:'',
      progress_txt: '',
      showGraduation:false,
      showGraduationMessage:false,
      showConfessioWall: false,
      showComplaints:false,
      getNoWatchedSuccess: false
  },

  /**
   * 查看了所有留言
   */
  view:function(e) {
    var whichOperation = e.currentTarget.dataset.whichoperation
    wx.navigateTo({
      url: '../leavingMessage/view/view?whichOperation=' + JSON.stringify(whichOperation),
    })
  },


  /**
   * 写留言
   */
  write: function (e) {
    //检查用户登录是否过期
    //utils.checksession();

    if (this.data.userId == null || app.globalData.userId == null) {
      wx.showModal({
        title: '登录提示',
        content: '请到【我的】登录',
        success: function (res) {
          if (res.confirm) {
            wx.switchTab({
              url: '../userPage/userPage',
            })
          }
        }
      })
    }
    else {
      var whichOperation = e.currentTarget.dataset.whichoperation
      console.log("whichOperation:" + whichOperation)
      wx.navigateTo({
        url: '../leavingMessage/write/write?whichOperation=' + JSON.stringify(whichOperation),
      })
    } 
  },

  /**
   * 吐槽
   */
  showComplaints:function(e) {
    var that = this
    var showComplaints = that.data.showComplaints
    that.setData({
      showComplaints: !showComplaints
    })
  },

  /**
   * 是否展示毕业留言
   */
  showGraduationMessage: function (e) {
    var that = this
    var showGraduationMessage = that.data.showGraduationMessage
    that.setData({
      showGraduationMessage: !showGraduationMessage
    })
  },


  /**
   * 是否展示表白墙
   */
  showConfessioWall: function (e) {
    var that = this
    var showConfessioWall = that.data.showConfessioWall
    that.setData({
      showConfessioWall: !showConfessioWall
    })
  },


  /**
   * 是否展示毕业信息
   */
  showGraduationSession:function(e) {
    var that = this
    var showGraduation = that.data.showGraduation
    that.setData({
      showGraduation:!showGraduation
    })
  },

  /**
   * 获取哪一届即将毕业
   */
  graduationSession:function(e) {
    var that = this
    var year = new Date().getFullYear()
    console.log("year:" + year)
    that.setData({
      graduationSession : year - 4
    })
  },

  /**
   * 毕业倒计时
   */
  graduationCountdown: function () {
    var that = this;

    this.data.intervarID = setInterval(function () {
      var year = new Date().getFullYear()
      //大学剩余时间
      var leftTime = ((new Date(year +'/06/20 00:00:00').getTime()) - (new Date().getTime())); //计算剩余的毫秒数
      //大学四年的时间
      var fullTime = (new Date(year + '/06/20 00:00:00').getTime()) - (new Date(that.data.graduationSession + '/07/28 00:00:00').getTime()) 
      //大学度过时间
      var spentTime = fullTime - leftTime

      //绘制两个圆圈
      that.drawProgressbg();
      that.drawCircle(spentTime / (fullTime / 2)),

      fullTime = fullTime / 1000
      leftTime = leftTime / 1000
      let day = "00"
      let hou = "00"
      let min = "00"
      let sec = "00"
      if (leftTime > 0) {
        // 获取天、时、分、秒
        day = parseInt(leftTime / (60 * 60 * 24));
        hou = parseInt(leftTime % (60 * 60 * 24) / 3600);
        min = parseInt(leftTime % (60 * 60 * 24) % 3600 / 60);
        sec = parseInt(leftTime % (60 * 60 * 24) % 3600 % 60);
        
        day = that.timeFormat(day),
        hou = that.timeFormat(hou),
        min = that.timeFormat(min),
        sec = that.timeFormat(sec)
      }
      that.setData({
        //四年时间计算
        fullTime_day : that.timeFormat(parseInt(fullTime / (60 * 60 * 24))),
        fullTime_hou : that.timeFormat(parseInt(fullTime / (60 * 60))),
        fullTime_min : that.timeFormat(parseInt(fullTime / (60))),
        fullTime_sec: fullTime,
        day:day,
        hou:hou,
        min: min,
        sec: sec,
        clock: day + "天" + hou + "小时" + min + "分" + sec + "秒"
      })
      if (day == '00' && hou == '00' && min == '00' && sec == '00') {
        clearInterval(that.data.intervarID);
      }      
    }, 1000)
  },
  
  timeFormat:function(param) {//小于10的格式化函数
    return param < 10 ? '0' + param : param;
  },

  checkTime:function (i) { //将0-9的数字前面加上0，例1变为01 
    if(i < 10) {
      i = "0" + i;
    }
    return i;
  },

  /**
   * 获取需求列表
   */
  getMessageList:function(e) {
    if (this.data.userId == null || app.globalData.userId == null) {
      wx.showModal({
        title: '登录提示',
        content: '请到【我的】登录',
        success: function (res) {
          if (res.confirm) {
            wx.switchTab({
              url: '../userPage/userPage',
            })
          }
        }
      })
    }
    else {
        var id = e.currentTarget.dataset.id
        console.log("id:" + id)
        
        var that = this;
        // 改变未读消息数量为0
        if (id == 1) {
          var allNoWatchedNo = that.data.allNoWatchedNo - that.data.applyNo
          that.setData({
            applyNo: 0,
            allNoWatchedNo: allNoWatchedNo
          })
        }
        else if (id == 2) {
          var allNoWatchedNo = that.data.allNoWatchedNo - that.data.evaluateNo
          that.setData({
            evaluateNo: 0,
            allNoWatchedNo: allNoWatchedNo
          })
        }
        else if (id == 3) {
          var allNoWatchedNo = that.data.allNoWatchedNo - that.data.overTimeNo
          that.setData({
            overTimeNo: 0,
            allNoWatchedNo: allNoWatchedNo
          })
        }
        else if (id == 4) {
          var allNoWatchedNo = that.data.allNoWatchedNo - that.data.overNumberNo
          that.setData({
            overNumberNo: 0,
            allNoWatchedNo: allNoWatchedNo
          })
        }
        else if (id == 5) {
          var allNoWatchedNo = that.data.allNoWatchedNo - that.data.eliminateNo
          that.setData({
            eliminateNo: 0,
            allNoWatchedNo: allNoWatchedNo
          })
        }
        console.log("allNoWatchedNo:" + that.data.allNoWatchedNo)
        if (that.data.allNoWatchedNo != 0) {
          wx.setTabBarBadge({
            index: 1,
            text: that.data.allNoWatchedNo.toString()
          })
        }
        else {
          wx.removeTabBarBadge({
            index: 1,
          })
        }
        wx.navigateTo({
          //url: '../applyMessageList/applyMessageList?categoryId=' + JSON.stringify(e.currentTarget.dataset.id) + '&isReply=' + JSON.stringify(e.currentTarget.dataset.isreply),
          url: '../messageDetails/messageDetails?categoryId=' + JSON.stringify(e.currentTarget.dataset.id) + '&isReply=' + JSON.stringify(e.currentTarget.dataset.isreply),
        })
    }
  },

  /**
   * 生命周期函数--监听页面加载
   */
  onLoad: function (options) {
      let that = this;
      that.setData({
        userId: app.globalData.userId,
      })

      //获取毕业是哪一届
      that.graduationSession()
      //毕业倒计时
      that.graduationCountdown() 

      //消息数量加载在onShow方法中
      //that.getUnreadMessageNo()
  },

  getUnreadMessageNo:function(e) {
    let that = this;
    console.log("messagePage页面getUnreadMessageNo方法 userId:" + that.data.userId)
    wx.request({
      url: app.globalData.host +'/MessageManage/getNoWatchedMessageNo?userId=' + that.data.userId,
      method: 'GET',
      header: {
        'content-type': 'application/json', // 默认值
        'token': app.globalData.token
      },
      success: (res) => {
        console.log("第一次获取未读消息数成功！！！！！！！！！！！！！！！！！！！！！！！！！！")
        if (res.data.isSuccess) {
          that.setData({
            allNoWatchedNo: res.data.data.allNoWatchedNo,
            applyNo: res.data.data.applyNo,
            evaluateNo: res.data.data.evaluateNo,
            overTimeNo: res.data.data.overTimeNo,
            overNumberNo: res.data.data.overNumberNo,
            eliminateNo: res.data.data.eliminateNo,
            userId: app.globalData.userId,
            getNoWatchedSuccess:true
          })
          console.log(that.data.allNoWatchedNo);

          console.log("allNoWatchedNo:" + that.data.allNoWatchedNo)
          if (that.data.allNoWatchedNo != 0) {
            wx.setTabBarBadge({
              index: 1,
              text: that.data.allNoWatchedNo.toString()
            })
          }
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
   * 绘画第一个背景圆环
   */
  drawProgressbg: function () {
    // 使用 wx.createContext 获取绘图上下文 context
    var ctx = wx.createCanvasContext('canvasProgressbg')
    ctx.setLineWidth(4);// 设置圆环的宽度
    ctx.setStrokeStyle('#20183b'); // 设置圆环的颜色
    ctx.setLineCap('round') // 设置圆环端点的形状
    ctx.beginPath();//开始一个新的路径
    ctx.arc(110, 110, 100, 0, 2 * Math.PI, false);
    //设置一个原点(100,100)，半径为90的圆的路径到当前路径
    ctx.stroke();//对当前路径进行描边
    ctx.draw();
  },
  
  /**
   * 绘画第二个圆环
   */
  drawCircle: function (step) {
    var context = wx.createCanvasContext('canvasProgress');
    // 设置渐变
    var gradient = context.createLinearGradient(200, 100, 100, 200);
    gradient.addColorStop("0", "#2661DD");
    gradient.addColorStop("0.5", "#40ED94");
    gradient.addColorStop("1.0", "#5956CC");
    context.setLineWidth(10);
    context.setStrokeStyle(gradient);
    context.setLineCap('round')
    context.beginPath();
    // 参数step 为绘制的圆环周长，从0到2为一周 。 -Math.PI / 2 将起始角设在12点钟位置 ，结束角 通过改变 step 的值确定
    context.arc(110, 110, 100, -Math.PI / 2, step * Math.PI - Math.PI / 2, false);
    context.stroke();
    context.draw()
  },

  /**
   * 生命周期函数--监听页面初次渲染完成
   */
  onReady: function () {
    let that = this;
    that.setData({
      userId: app.globalData.userId,
    })
  },

  /**
   * 生命周期函数--监听页面显示
   */
  onShow: function () {
    let that = this;
    that.setData({
      userId: app.globalData.userId,
    })
    //获取未读消息数量
    //当用户登录时，每一秒刷新一次
    if (that.data.userId != null || app.globalData.userId != null) {
      //每1秒刷新一次页面
      that.data.getUnreadMessageNo = setInterval(function () {
        that.getUnreadMessageNo()
      }, 1000)
    }
  },

  /**
   * 生命周期函数--监听页面隐藏
   */
  onHide: function () {
    var that = this
    //清除计时器  即清除intervar
    clearInterval(that.data.getUnreadMessageNo);
  },

  /**
   * 生命周期函数--监听页面卸载
   */
  onUnload: function () {
    var that = this
    //清除计时器  即清除intervarID
    clearInterval(that.data.getUnreadMessageNo);
  },

  /**
   * 页面相关事件处理函数--监听用户下拉动作
   */
  onPullDownRefresh: function () {
    let that = this;
    wx.showNavigationBarLoading()
    setTimeout(function() {
      //获取未读消息数量
      that.getUnreadMessageNo()
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