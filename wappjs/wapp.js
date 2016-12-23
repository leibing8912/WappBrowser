(function () {
    /**
     * wapp��һ�� JS SDK ����������ָ��APP��webҳ����sdk�ı���֧��������ָ��APP�ṩ��
     * @name wapp
     * @author swallow email = swallowzhhy@gmail.com
     * @class ����InappBrowser����֧��SDK
     */
    var wapp = window.wapp = {};
    /**
     * widget��Ϊҳ���ṩ���صĿؼ�֧�֣�����Ի���ͼƬ����ؼ���
     * @memberOf wapp
     * @class �ؼ�֧��API����
     */
    wapp.widget = {};
    /**
     * function��Ϊҳ���ṩ���صĹ���֧�֣����������¼��
     * @memberOf wapp
     * @class ����֧��API����
     */
    wapp.function = {};
    /**
     * control��Ϊҳ���ṩ���صĿ�֧�֣�����򿪡��ر�ҳ�棬���ص�
     * @memberOf wapp
     * @class ����֧��API����
     */
    wapp.control = {};
    /**
     * utils��Ϊҳ���ṩһЩ���߷�����֧�֣����紥��ĳ���¼�
     * @memberOf wapp
     * @class ����֧��API����
     */
    wapp.utils = {};
    /**
     * Ҫ�򿪵�ҳ������ = �ⲿ������Webҳ
     * @see http://prototype.ym/czh5.0/#p=h5��ܹ淶
     * @constant
     * @memberOf wapp
     * @type {Number}
     */
    wapp.PAGE_TYPE_EXTERN = 0;
    /**
     * Ҫ�򿪵�ҳ������ = Web APP��Webҳ
     * @see http://prototype.ym/czh5.0/#p=h5��ܹ淶
     * @constant
     * @memberOf wapp
     * @type {Number}
     */
    wapp.PAGE_TYPE_WEBAPP = 1;
    /**
     * Ҫ�򿪵�ҳ������ = APP��Webҳ
     * @see http://prototype.ym/czh5.0/#p=h5��ܹ淶
     * @constant
     * @memberOf wapp
     * @type {Number}
     */
    wapp.PAGE_TYPE_APP = 2;
    /**
     * Ҫ�򿪵�ҳ������ = ����ҳ��
     * @constant
     * @memberOf wapp
     * @type {Number}
     */
    wapp.PAGE_TYPE_NATIVE = 3;
    // wapp.callbacks = {};
    //һЩ����
    var scheme = 'czh://';
    var version = '1.0.0';
    var vender = 'ym';
    var isReady = false;
    var isDebug = false;
    var callbacks = {};
    var listeners = [];
    listeners['ready']=[];
    listeners['error']=[];
    listeners['resume']=[];
    listeners['paused']=[];
    /**
     * ��ʼ���ӿڣ��������ڵ������sdk��api֮ǰ�����ȵ������init�ӿ�
     * @function
     * @memberOf wapp
     * @param debug �Ƿ�������ģʽ������ģʽ����һЩ�ط�����alert.
     */
    wapp.init = function (debug) {
        isDebug = debug;
        wapp.utils.addEventListener('ready', function () {
            if (isDebug)
                alert('wapp is ready!');
            isReady = true;
        });
    };
// ---------------------------------H5 page API---------------------------------
// These methods provide to the H5 page developer user
    /**
     * ����һ�����ضԻ���
     * @param title �Ի������
     * @param message �Ի�����Ϣ
     * @param cancel ȡ��
     * @param buttons ��ť
     * @param callback �ص�
     * <p>
     *      callback(index)
     *          index: ����İ�ť�ţ�cancel=0; buttons=[1, 2, ...]
     * </p>
     */
    wapp.widget.alert = function (title, message, cancel, buttons, callback) {
        var host = 'alert.widget.native';
        var params = {
            title: title,
            message: message,
            cancel: cancel,
            buttons: JSON.stringify(buttons),
            callback: createCbKey(callback, {index:{}})
        };
        var uri = createUri(scheme, host, params);
        nativeCall(uri);
    };
    /**
     * �ñ��ؿؼ����һ�Ŵ�ͼ
     * @param imgUrl ��ͼ��ַ
     */
    wapp.widget.img = function (imgUrl) {
        var host = 'img.widget.native';
        var params = {
            url: imgUrl
        };
        var uri = createUri(scheme, host, params);
        nativeCall(uri);
    };
    /**
     * ���𱾵�΢�ŷ�����
     * @param shareType ��������
     * @param callback �������ص�
     */
    wapp.function.wxShare = function (shareType, callback) {
        var host = 'wx_share.function.native';
        var params = {
            type: shareType,
            callback: createCbKey(callback)
        };
        var uri = createUri(scheme, host, params);
        nativeCall(uri);
    };
    /**
     * ��ȡ�û���Ϣ
     * @param callback
     * <p>
     *     callback(info)
     *          info: һ��json����
     * </p>
     */
    wapp.function.getUserInfo = function (callback) {
        var host = 'user_info.function.native';
        var params = {
            callback: createCbKey(callback, {
                info:{}
            })
        };
        var uri = createUri(scheme, host, params);
        nativeCall(uri);
    };
    /**
     * �˳���¼
     */
    wapp.function.logout = function () {
        var host = 'log_out.function.native';
        var uri = createUri(scheme, host, null);
        nativeCall(uri);
    };
    /**
     * ������
     * @param message �����ߵ���Ϣ����������api���ص���Ϣһ��
     */
    wapp.function.kickout = function (message) {
        var host = 'kickout.function.native';
        var params = {
            message: message
        };
        var uri = createUri(scheme, host, params);
        nativeCall(uri);
    };
    /**
     * ��������
     * @param path ����·��
     * @param queryParams �������, ��json��ʽ��֯�����磺{user:'swallow', password:'123456'}
     * @param callback ����ص�
     * <p>
     *     callback(response)
     *          response:һ��json����
     * </p>
     */
    wapp.function.request = function (path, queryParams, callback) {
        var host = 'request.function.native';
        var params = {
            path: path,
            params: queryParams,
            callback:createCbKey(callback, {
                response:{}
            })
        };
        var uri = createUri(scheme, host, params);
        nativeCall(uri);
    };
    /**
     * ��һ��ҳ�棬��������ҳ�棬Webҳ
     * @param type ҳ������{@link http://prototype.ym/czh5.0/#p=h5��ܹ淶}
     * @param title ҳ�����
     * @param url ҳ���ַ
     */
    wapp.control.open = function (type, title, url) {
        var host = 'open.control.native';
        var params = {
            type: type,
            title: title,
            url: url
        };
        var uri = createUri(scheme, host, params);
        nativeCall(uri);
    };
    /**
     * �ر�һ��ҳ��
     */
    wapp.control.close = function () {
        var host = 'close.control.native';
        var uri = createUri(scheme, host, null);
        nativeCall(uri)
    };
    /**
     * ����һҳ��
     */
    wapp.control.goBack = function () {
        var host = 'back.control.native';
        var uri = createUri(scheme, host, null);
        nativeCall(uri);
    };
    /**
     * ���ص�App��ҳ
     */
    wapp.control.goHome = function () {
        var host = 'home.control.native';
        var uri = createUri(scheme, host, null);
        nativeCall(uri);
    };
    /**
     * ���ⲿ��ͼ����λ��һ����ַ
     * @param address {string} Ҫ��λ���ĵ�ַ
     */
    wapp.control.openMap = function (address) {
        var host = 'map.control.native';
        var params = {
            address:address
        };
        var uri = createUri(scheme, host, params);
        nativeCall(uri);
    };
// ---------------------------------Util Methods---------------------------------
// These methods is the util methods
    /**
     * ��ȡwapp�汾
     * @function
     * @returns {string} wapp�汾
     */
    wapp.utils.getVersion = function () {
        return version;
    };
    /**
     * ��ȡ�ṩ��
     * @returns {string} �ṩ�̼�д
     */
    wapp.utils.getVender = function () {
        return vender;
    };
    /**
     * ����һ�������¼�
     * @param message ������Ϣ
     * @param where ��������ĵط�
     */
    wapp.utils.errorEvent = function (message, where) {
        fireEvent('error', message, where);
    };
    /**
     * ����һ��׼���¼�
     */
    wapp.utils.readyEvent = function () {
        fireEvent('ready');
    };
    /**
     * ����һ��ҳ��ָ��¼�
     */
    wapp.utils.resumeEvent = function () {
        fireEvent('resume');
    };
    /**
     * ����һ��ҳ����ͣ�¼�
     */
    wapp.utils.pausedEvent = function () {
        fireEvent('paused');
    };
    /**
     * ���һ���¼�����
     * @param event_name Ҫ�������¼���
     * @param method �¼���������
     */
    wapp.utils.addEventListener = function (event_name, method){
        if(listeners[event_name].indexOf(method) > -1) return; // Listener is already registered
        listeners[event_name].push(method);
    };
    /**
     * �Ƴ��¼�����
     * @param event_name �������¼�����
     * @param method Ҫ�Ƴ��ļ�������
     */
    wapp.utils.removeEventListener = function (event_name, method){
        //If no method name is given, remove all listeners from event
        if(method == null){
            listeners[event_name].length=0;
            return;
        }
        var method_index = listeners[event_name].indexOf(method);
        if(method_index > -1){ //Don't try to remove unregistered listeners
            listeners[event_name][method_index] = null;
        }else{
            wapp.utils.errorEvent("An unregistered listener was requested to be removed.", "removeEventListener()")
        }
    };
    wapp.utils.runCb = function (index) {
        if (!callbacks[index])
            return;
        var args = Array.prototype.slice.call(arguments);
        args.shift();
        if (typeof callbacks[index] === "function")
            callbacks[index].apply(null, args);
        delete callbacks[index];
    };
// ---------------------------------Private Methods---------------------------------
// These methods will not provide to the sdk user
    function createUri(scheme, host, params) {
        var result = scheme + host + '';
        //�޲�
        if (params == null)
            return result;
        result += '?';
        for (var key in params) {
            result += key + '=' + params[key] + '&';
        }
        //ȥ����β����ġ�&��
        if (result.lastIndexOf('&') == (result.length-1))
            result = result.substring(0, result.lastIndexOf('&'));
        return result;
    }
    function nativeCall(uri) {
        if (isReady)
            window.open(uri);
        else
            wapp.utils.errorEvent('The wapp is not ready, it will not work!', 'nativeCall()');
    }
    function randStr() {
        var functionStr = "";
        for (var i = 0; i < parseInt(Math.random() * (20 - 5 + 1) + 5, 10); i++) {
            functionStr = functionStr + String.fromCharCode(parseInt(Math.random() * (57 - 48 + 1) + 48, 10))
        }
        return functionStr
    }
    function createCbKey(cb, params) {
        var random = randStr();
        var cbKey = "wapp.utils.runCb('" + random + "')";
        if (params != null) {
            cbKey = "wapp.utils.runCb('" + random + "',";
            for (var key in params) {
                cbKey += key+',';
            }
            cbKey = cbKey.substring(0, cbKey.lastIndexOf(','));
            cbKey += ')';
        }
        callbacks[random] = cb;
        return cbKey;
    }
    // ------------------------- Listener -------------------------
    function fireEvent(event){
        if(!listeners[event]){
            return;
        }
        var args = Array.prototype.slice.call(arguments);
        args.shift();
        var length = listeners[event].length;
        for(var i=0; i<length; i++){
            if(typeof listeners[event][i] === "function"){
                listeners[event][i].apply(null, args);
            }
        }
    }
})();