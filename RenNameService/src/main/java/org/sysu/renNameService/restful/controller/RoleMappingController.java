/*
 * Project Ren @ 2018
 * Rinkako, Ariana, Gordan. SYSU SDCS.
 */
package org.sysu.renNameService.restful.controller;
import org.springframework.web.bind.annotation.*;
import org.sysu.renNameService.entity.RenRolemapEntity;
import org.sysu.renNameService.restful.dto.ReturnElement;
import org.sysu.renNameService.restful.dto.ReturnModel;
import org.sysu.renNameService.restful.dto.StatusCode;
import org.sysu.renNameService.roleMapping.RoleMappingService;
import org.sysu.renNameService.utility.SerializationUtil;
import org.sysu.renNameService.utility.TimestampUtil;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: Gordan
 * Date  : 2018/1/19
 * Usage : Handle requests about business role and worker mappings.
 */

@RestController
@RequestMapping("/rolemap")
public class RoleMappingController {

    public ReturnModel ExceptionHandlerFunction(String exception) {
        ReturnModel rnModel = new ReturnModel();
        rnModel.setCode(StatusCode.Exception);
        rnModel.setRs(TimestampUtil.GetTimeStamp() + " 0");

        ReturnElement returnElement = new ReturnElement();
        returnElement.setMessage(exception);
        rnModel.setReturnElement(returnElement);

        return rnModel;
    }

    public ReturnModel HandleMissingParameters(List<String> params) {
        ReturnModel rnModel = new ReturnModel();
        rnModel.setCode(StatusCode.Fail);
        rnModel.setRs(TimestampUtil.GetTimeStamp() + " 0");
        ReturnElement returnElement = new ReturnElement();
        StringBuffer sb = new StringBuffer();
        sb.append("miss parameters:");
        for (String s : params) {
            sb.append(s+" ");
        }
        returnElement.setMessage(sb.toString());
        rnModel.setReturnElement(returnElement);
        return rnModel;
    }

    /**
     *
     * @param rtid
     * @param brole
     * @return Get worker's id by his business role.
     */
    @RequestMapping(value = "/getWorkerByBRole", produces = {"application/json", "application/xml"})
    @ResponseBody
    public ReturnModel GetWorkerByBusinessRole(@RequestParam(value="rtid", required = false)String rtid,
                                               @RequestParam(value="brole", required = false)String brole) {
        ReturnModel rnModel = new ReturnModel();
        try {
            // miss params
            List<String> missingParams = new ArrayList<>();
            if (rtid == null) missingParams.add("rtid");
            if (brole == null) missingParams.add("brole");
            if (missingParams.size() > 0) {
                rnModel = HandleMissingParameters(missingParams);
                return rnModel;
            }

            rnModel.setCode(StatusCode.OK);
            rnModel.setRs(TimestampUtil.GetTimeStamp() + " 0");
            ReturnElement returnElement = new ReturnElement();
            returnElement.setData("GetWorkerByBusinessRole");
            rnModel.setReturnElement(returnElement);
        } catch (Exception e) {
            rnModel = ExceptionHandlerFunction(e.getClass().getName());
        }

        return rnModel;
    }

    /**
     * Get business role by the worker's id.
     * @param rtid
     * @param gid
     * @return
     */
    @RequestMapping(value = "/getBRoleByWorker", produces = {"application/json", "application/xml"})
    @ResponseBody
    public ReturnModel GetBusinessRoleByGlobalId(@RequestParam(value="rtid", required = false)String rtid,
                                                 @RequestParam(value="gid", required = false)String gid) {
        ReturnModel rnModel = new ReturnModel();
        try {
            // miss params
            List<String> missingParams = new ArrayList<>();
            if (rtid == null) missingParams.add("rtid");
            if (gid == null) missingParams.add("gid");
            if (missingParams.size() > 0) {
                rnModel = HandleMissingParameters(missingParams);
                return rnModel;
            }

            rnModel.setCode(StatusCode.OK);
            rnModel.setRs(TimestampUtil.GetTimeStamp() + " 0");
            ReturnElement returnElement = new ReturnElement();
            returnElement.setData("GetBusinessRoleByGlobalId");
            rnModel.setReturnElement(returnElement);
        } catch (Exception e) {
            rnModel = ExceptionHandlerFunction(e.getClass().getName());
        }

        return rnModel;
    }

    /**
     * Register a mapping to RoleMap Service.
     * @param rtid
     * @param map
     * @return
     */
    @RequestMapping(value = "/register", produces = {"application/json", "application/xml"})
    @ResponseBody
    public ReturnModel RegisterRoleMapService(@RequestParam(value="rtid", required = false)String rtid,
                                              @RequestParam(value="map", required = false)String map) {
        ReturnModel rnModel = new ReturnModel();
        try {
            // miss params
            List<String> missingParams = new ArrayList<>();
            if (rtid == null) missingParams.add("rtid");
            if (map == null) missingParams.add("map");
            if (missingParams.size() > 0) {
                rnModel = HandleMissingParameters(missingParams);
                return rnModel;
            }

            rnModel.setCode(StatusCode.OK);
            rnModel.setRs(TimestampUtil.GetTimeStamp() + " 0");
            ReturnElement returnElement = new ReturnElement();
            returnElement.setData("RegisterRoleMapService");
            rnModel.setReturnElement(returnElement);
        } catch (Exception e) {
            rnModel = ExceptionHandlerFunction(e.getClass().getName());
        }

        return rnModel;
    }

    /**
     * Finish a process and delete cache.
     * @param rtid
     * @return
     */
    @RequestMapping(value = "/fin", produces = {"application/json", "application/xml"})
    @ResponseBody
    @Transactional
    public ReturnModel FinishRoleMapService(@RequestParam(value="rtid", required = false)String rtid) {
        ReturnModel rnModel = new ReturnModel();
//        try {
            // miss params
            List<String> missingParams = new ArrayList<>();
            if (rtid == null) missingParams.add("rtid");
            if (missingParams.size() > 0) {
                rnModel = HandleMissingParameters(missingParams);
                return rnModel;
            }

            RoleMappingService.FinishRoleMapService("AA1");

            rnModel.setCode(StatusCode.OK);
            rnModel.setRs(TimestampUtil.GetTimeStamp() + " 0");
            ReturnElement returnElement = new ReturnElement();
            returnElement.setData("FinishRoleMapService");
            rnModel.setReturnElement(returnElement);
//        } catch (Exception e) {
//            rnModel = ExceptionHandlerFunction(e.getClass().getName());
//        }

        return rnModel;
    }

    /**
     * Get all resources involved in a process.
     * @param rtid
     * @return
     */
    @RequestMapping(value = "/getInvolved", produces = {"application/json", "application/xml"})
    @ResponseBody
    @Transactional
    public ReturnModel GetInvolvedResource(@RequestParam(value="rtid", required = false)String rtid) {
        ReturnModel rnModel = new ReturnModel();
        try {
            // miss params
            List<String> missingParams = new ArrayList<>();
            if (rtid == null) missingParams.add("rtid");
            if (missingParams.size() > 0) {
                rnModel = HandleMissingParameters(missingParams);
                return rnModel;
            }

            ArrayList<RenRolemapEntity> involves = RoleMappingService.GetInvolvedResource(rtid);
            String jsonifyInvolves = SerializationUtil.JsonSerilization(involves);

            rnModel.setCode(StatusCode.OK);
            rnModel.setRs(TimestampUtil.GetTimeStamp() + " 0");
            ReturnElement returnElement = new ReturnElement();
            returnElement.setData(jsonifyInvolves);
            rnModel.setReturnElement(returnElement);
        } catch (Exception e) {
            rnModel = ExceptionHandlerFunction(e.getClass().getName());
        }

        return rnModel;
    }

}
