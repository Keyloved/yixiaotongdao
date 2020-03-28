const app = getApp()
/**
 * 检查用户登录是否过期
 */
//验证登录是否过期
function checksession() {
  wx.checkSession({
    success: function (res) {
      console.log(res, '登录未过期')
    },
    fail: function (res) {
      //设置系统userId为空
      app.globalData.userId = null

      console.log(res, '登录过期了')
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
  })
}