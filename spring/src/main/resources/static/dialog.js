/**
 * $.dialog
 * @extends jquery.1.9.0
 * @fileOverview 弹出层
 * @author BW
 * @email
 * @site
 * @version 0.1
 * @date 2013-12-18
 * Copyright (c) 2013-2013 BW
 * @example
 * $("#box").dialog({
       move: true,
       title: "添加分类",
       buttons: [{
         name: "重置",
         callback: function() {
           alert("重置");
         }
       }, {
         name: "上一步",
         callback: function() {
           alert("上一步");
         }
       }]
   }).dialog("open");
 */
(function($) {
  var idName;
  $.fn.dialog = function() {
    var method = arguments[0];
    if (methods[method]) {
      method = methods[method];
      arguments = Array.prototype.slice.call(arguments, 1);
    } else if (typeof(method) == "object" || !method) {
      method = methods.init;
    } else {
      $.error("Method" + method + "does not exist on jQuery.dialog");
      return this;
    }
    return method.apply(this, arguments);
  };
 
  var methods = {
    init: function(options) {
      var defaults = {
        move: false,
        lock: false,
        width: "auto",
        height: "auto",
        title: false,
        content: "url:content.html",
        showBtn: true,
        okText: "确认",
        cancelText: "取消",
        ok: function() {},
        cancel: function() {
          methods.close();
        },
        buttons: []
      };
 
      var settings = $.extend({}, defaults, options);
 
      return this.each(function() {
        var $this = $(this);
        idName = $this.attr("id");
        var isIframe = settings.content.indexOf("url:");
 
        var drawDialog = function() {
          $this.addClass("green-ui-dialog");
          //标题BOX
          if (typeof(settings.title) == "string") {
            var titleBox = "<div id=\"" + idName + "-titleBox\" class=\"green-ui-dialog-titleBox\"><h3>" + settings.title + "</h3><p><span id=\"" + idName + "-close\">X</span></p></div>";
            $this.append($(titleBox));
            $("#" + idName).on("click", "#box-close", function() {
              if (settings.lock == true) {
                $("#" + idName + "-lock").remove();
              }
              methods.close();
            });
            $("#" + idName + "-close").mouseover(function() {
              $("#" + idName + "-close").addClass("green-ui-dialog-close");
            }).mouseout(function() {
              $("#" + idName + "-close").removeClass("green-ui-dialog-close");
            });
 
            if (settings.move == true) {
              $("#" + idName + "-titleBox").mouseover(function() {
                $this.css("cursor", "move");
              }).mouseout(function() {
                $this.css("cursor", "default");
              });
 
              $("#" + idName + "-titleBox").mousedown(function(event) {
                var offset = $("#" + idName).offset();
                var x1 = event.clientX - offset.left;
                var y1 = event.clientY - offset.top;
                var btnNum = event.button;
                if (btnNum == 0) {
                  $(document).mousemove(function(event) {
                    $this.css({
                      "left": (event.clientX - x1) + "px",
                      "top": (event.clientY - y1) + "px",
                      "margin": "0"
                    });
                  });
                }
              }).mouseup(function() {
                $(document).unbind("mousemove");
              });
            }
 
 
 
          }
 
          //内容BOX
          var contentBox = "<div id=\"" + idName + "-contentBox\" class=\"green-ui-dialog-contentBox\"></div>";
          $this.append($(contentBox));
 
 
          //按钮BOX
          if (settings.showBtn == true) {
            var buttonBox = $("<div id=\"" + idName + "-buttonBox\" class=\"green-ui-dialog-buttonBox\"></div>");
            var okBtn = idName + "-btn-ok";
            var cancelBtn = idName + "-btn-cancel";
            var allBtns = new Array(okBtn, cancelBtn);
            $("<p><span id=\"" + cancelBtn + "\" class=\"green-ui-dialog-button\">" + settings.cancelText + "</span></p>").appendTo(buttonBox);
            $("<p><span id=\"" + okBtn + "\" class=\"green-ui-dialog-button\">" + settings.okText + "</span></p>").appendTo(buttonBox);
 
            $("#" + idName).on("click", "#" + okBtn, function() {
              settings.ok();
            });
            $("#" + idName).on("click", "#" + cancelBtn, function() {
              if (settings.lock == true) {
                $("#" + idName + "-lock").remove();
              }
              settings.cancel();
            });
 
            var count = settings.buttons.length;
            if (count > 0) {
              $.each(settings.buttons, function(k, v) {　　
                var str = idName + "-btn-" + k;
                allBtns.push(str);
                $("<p><span id=\"" + str + "\" class=\"green-ui-dialog-button\">" + v.name + "</span></p>").appendTo(buttonBox);
                $("#" + idName).on("click", "#" + str, function() {
                  v.callback();
                });
              });
 
            }
            $this.append(buttonBox);
 
            $.each(allBtns, function(k, v) {　　
              $("#" + v).mouseover(function() {
                $("#" + v).addClass("green-ui-dialog-button-change");
              }).mouseout(function() {
                $("#" + v).removeClass("green-ui-dialog-button-change");
              });
            });
 
          }
 
          //内容BOX里面的span || iframe
          if (isIframe == -1) {
            $("<span>" + settings.content + "</span>").appendTo($("#" + idName + "-contentBox"));
          } else {
            var urlStr = settings.content.split("url:")[1];
            $("<iframe id=\"" + idName + "-iframeBox\" src=\"" + urlStr + "\" scrolling=\"no\"></iframe>").appendTo($("#" + idName + "-contentBox"));
            $("#" + idName + "-iframeBox").load(function() {
              var iframeBoxHeight = $("#" + idName + "-iframeBox").contents().find("body div:first").height() + 2;
              var iframeBoxWidth = $("#" + idName + "-iframeBox").contents().find("body div:first").width() + 2;
              $("#" + idName + "-iframeBox").height(iframeBoxHeight).width(iframeBoxWidth);
              var boxWidthAndHeight = autoWidthAndHeight(settings.width, settings.height);
              var boxWidth = boxWidthAndHeight[0];
              var boxHeight = boxWidthAndHeight[1];
              $this.css({
                "left": "50%",
                "top": "50%",
                "margin": "-" + (boxHeight / 2) + "px 0 0 -" + (boxWidth / 2) + "px",
                "width": boxWidth + "px",
                "height": boxHeight + "px",
                "z-index": "10001"
              });
 
            });
 
          }
 
 
        };
 
        var autoWidthAndHeight = function(width, height) {
          var boxWidth, boxHeight;
          if (typeof(width) == "number") {
            $this.css("width", width);
            boxWidth = width;
          } else {
            boxWidth = $("#" + idName).width();
          }
          if (typeof(height) == "number") {
            var titleBoxHeight = 0;
            var btnBoxHeight = 0;
            if (typeof(settings.title) == "string") {
              titleBoxHeight = $("#" + idName + "-titleBox").height();
            }
            if (settings.showBtn == true) {
              btnBoxHeight = $("#" + idName + "-buttonBox").height();
            }
            var contentBoxHeight = height - titleBoxHeight - btnBoxHeight - 5;
 
            $this.css("height", height);
            $("#" + idName + "-contentBox").css("height", contentBoxHeight + "px");
            $("#" + idName + "-buttomBox").css({
              "position": "absolute",
              "bottom": "0px"
            });
            boxHeight = height;
          } else {
            boxHeight = $("#" + idName).height();
          }
 
          var boxWidthAndHeight = new Array(boxWidth, boxHeight);
          return boxWidthAndHeight;
        };
 
        if (settings.lock == true) {
          $("body").append($("<div id=\"" + idName + "-lock\"></div>"));
          $("#" + idName + "-lock").css({
            "display": "block",
            "position": "absolute",
            "background-color": "gray",
            "top": "0px",
            "left": "0px",
            "width": "100%",
            "height": "100%",
            "z-index": "10000"
          });
        }
        drawDialog();
 
      });
 
    },
    open: function() {
      return this.each(function() {
        var $this = $(this);
        $this.css("display", "inline");
      });
    },
    close: function() {
      $("#" + idName).remove();
    }
  };
 
})(jQuery);