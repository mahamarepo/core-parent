package com.mahama.parent.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.mahama.common.enumeration.MimeType;
import com.mahama.common.exception.ServiceExceptionEnum;
import com.mahama.common.utils.Assert;
import com.mahama.common.utils.ByteUtil;
import com.mahama.parent.enumeration.BizExceptionEnum;
import com.mahama.parent.utils.EhCacheUtil;
import com.mahama.parent.vo.PageJsonRet;
import com.mahama.parent.vo.PageRet;
import com.mahama.parent.vo.Ret;
import com.mahama.parent.vo.Rets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Slf4j
public abstract class BaseController {
    public <T> Ret<T> success(Optional<T> data) {
        return data.map(Rets::success).orElseGet(() -> Rets.success(null));
    }

    public <T> Ret<List<T>> success(List<T> data) {
        return Rets.success(data);
    }

    public <T> Ret<PageRet<List<T>>> success(PageRet<List<T>> data) {
        return Rets.success(data);
    }

    public <T> Ret<T> success(T data) {
        return Rets.success(data);
    }

    public <T> Ret<T> success() {
        return Rets.success();
    }

    public <T> Ret<PageRet<List<T>>> success(Page<T> page) {
        return success(new PageRet<>(page.getContent(), page.getTotalElements(), page.getNumber() + 1, page.getSize()));
    }

    public <T, M> Ret<PageRet<List<M>>> success(Page<T> page, List<M> list) {
        return success(new PageRet<>(list, page.getTotalElements(), page.getNumber() + 1, page.getSize()));
    }

    public <T> Ret<PageRet<List<T>>> success(IPage<T> page) {
        return success(new PageRet<>(page.getRecords(), page.getTotal(), page.getCurrent(), page.getSize()));
    }

    public <T, M> Ret<PageRet<List<M>>> success(IPage<T> page, List<M> list) {
        return success(new PageRet<>(list, page.getTotal(), page.getCurrent(), page.getSize()));
    }

    public Ret<PageJsonRet> success(PageJsonRet data) {
        return Rets.success(data);
    }

    public <T> Ret<PageJsonRet> success(Page<T> page, JSONArray jsonArray) {
        return success(new PageJsonRet(jsonArray, page.getTotalElements(), page.getNumber() + 1, page.getSize()));
    }

    public <T> Ret<PageJsonRet> success(IPage<T> page, JSONArray jsonArray) {
        return success(new PageJsonRet(jsonArray, page.getTotal(), page.getCurrent(), page.getSize()));
    }

    public <T> Ret<T> success(T data, String msg) {
        return Rets.success(data, msg);
    }

    public <T> Ret<JSONObject> success(T data, Integer page, Integer perpage, Integer totle) {
        JSONObject json = new JSONObject();
        json.put("data", data);
        json.put("total", totle);
        json.put("page", page);
        json.put("limit", perpage);
        return success(json);
    }

    public Ret<JSONObject> success(JSONObject json) {
        return Rets.success(json);
    }

    public Ret<JSONArray> success(JSONArray json) {
        return Rets.success(json);
    }

    public <T> Ret<T> successByEhCache(String cache, String key, EhCacheUtil.EhCacheTask<T> cacheTask) {
        return successByEhCache("none", cache, key, cacheTask);
    }

    public <T> Ret<T> successByEhCache(Long group, String cache, String key, EhCacheUtil.EhCacheTask<T> cacheTask) {
        return successByEhCache(group + "", cache, key, cacheTask);
    }

    public <T> Ret<T> successByEhCache(String group, String cache, String key, EhCacheUtil.EhCacheTask<T> cacheTask) {
        return Rets.success(EhCacheUtil.getCache(
                group,
                cache,
                key,
                cacheTask
        ));
    }

    public <T> Ret<T> failure(String msg) {
        return Rets.failure(msg);
    }

    public <T> Ret<T> failure(ServiceExceptionEnum exceptionEnum) {
        return Rets.failure(exceptionEnum.getMessage());
    }

    public <T> Ret<T> failure(Integer rc, String msg) {
        return Rets.failure(rc, msg);
    }

    public void download(HttpServletResponse response, InputStream inputStream, String fileName, MimeType mimeType) {
        Assert.notNull(inputStream, BizExceptionEnum.FILE_READING_ERROR);
        try {
            response.setCharacterEncoding("UTF-8");
            if (mimeType == null) {
                mimeType = MimeType.download;
            }
            response.setContentType(mimeType.getValue() + ";charset=utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setHeader("Pragma", "No-cache");
            OutputStream outputStream = response.getOutputStream();
            byte[] all = new byte[0];
            byte[] read = new byte[1024];
            while (inputStream.read(read) > -1) {
                all = ByteUtil.concat(all, read);
            }
            outputStream.write(all);
            outputStream.close();
        } catch (IOException e) {
            log.error("下载文件出错：{}", e.getMessage());
        }
    }
}
