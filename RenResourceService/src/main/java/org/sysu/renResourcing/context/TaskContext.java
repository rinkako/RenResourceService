/*
 * Project Ren @ 2018
 * Rinkako, Ariana, Gordan. SYSU SDCS.
 */
package org.sysu.renResourcing.context;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.sysu.renResourcing.consistency.RuntimeContextCachePool;
import org.sysu.renResourcing.context.steady.RenBoEntity;
import org.sysu.renResourcing.context.steady.RenRstaskEntity;
import org.sysu.renResourcing.context.steady.RenRuntimerecordEntity;
import org.sysu.renResourcing.utility.CommonUtil;
import org.sysu.renResourcing.utility.HibernateUtil;
import org.sysu.renResourcing.utility.LogUtil;
import org.sysu.renResourcing.utility.SerializationUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Author: Rinkako
 * Date  : 2018/2/4
 * Usage : Task context is an encapsulation of RenRSTaskEntity in a
 *         convenient way for resourcing service.
 */
public class TaskContext implements Serializable, RCacheablesContext {
    /**
     * Serial version UID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Task global id.
     */
    private String taskGlobalId;

    /**
     * Task id, unique in a process.
     */
    private String taskId;

    /**
     * Task name, unique in a process except base BO, for polymorphism.
     */
    private String taskName;

    /**
     * Task resourcing principle.
     */
    private String principle;

    /**
     * Global id of Belong to which BO.
     */
    private String boid;

    /**
     * Global id of Belong to which Process.
     */
    private String pid;

    /**
     * Task documentation.
     */
    private String documentation;

    /**
     * Notification hook dictionary. (Change, NotifyURL)
     */
    private HashMap<String, String> hooks = new HashMap<>();

    /**
     * Callback event dictionary. (Status, EventName)
     */
    private HashMap<String, String> callbacks = new HashMap<>();

    /**
     * Parameters vector.
     */
    private ArrayList<String> parameters = new ArrayList<>();

    /**
     * Get a task context by its name and belonging BO name of one runtime.
     * @param rtid runtime record id
     * @param boName belong to BO id
     * @param taskName task name
     * @return Task resourcing context, null if exception occurred or assertion error
     */
    public static TaskContext GetContext(String rtid, String boName, String taskName) {
        return TaskContext.GetContext(rtid, boName, taskName, false);
    }

    /**
     * Get a task context by its name and belonging BO name of one runtime.
     * @param rtid runtime record id
     * @param boName belong to BO id
     * @param taskName task name
     * @return Task resourcing context, null if exception occurred or assertion error
     */
    public static TaskContext GetContext(String rtid, String boName, String taskName, boolean forceReload) {
        String taskCtxId = String.format("%s_%s_%s", rtid, boName, taskName);
        TaskContext cachedCtx = RuntimeContextCachePool.Retrieve(TaskContext.class, taskCtxId);
        // fetch cache
        if (cachedCtx != null && !forceReload) {
            return cachedCtx;
        }
        Session session = HibernateUtil.GetLocalThreadSession();
        Transaction transaction = session.beginTransaction();
        boolean cmtFlag = false;
        try {
            RenRuntimerecordEntity rre = session.get(RenRuntimerecordEntity.class, rtid);
            assert rre != null;
            String pid = rre.getProcessId();
            RenBoEntity rbe = (RenBoEntity) session.createQuery(String.format("FROM RenBoEntity WHERE pid = '%s' AND boName = '%s'", pid, boName)).uniqueResult();
            assert rbe != null;
            RenRstaskEntity taskEntity = (RenRstaskEntity) session.createQuery(String.format("FROM RenRstaskEntity WHERE boid = '%s' AND polymorphismName = '%s'", rbe.getBoid(), taskName)).uniqueResult();
            assert taskEntity != null;
            transaction.commit();
            cmtFlag = true;
            TaskContext generatedCtx = TaskContext.GenerateTaskContext(taskEntity, pid);
            RuntimeContextCachePool.AddOrUpdate(taskCtxId, generatedCtx);
            return generatedCtx;
        }
        catch (Exception ex) {
            if (!cmtFlag) {
                transaction.rollback();
            }
            LogUtil.Log("When json serialization exception occurred, transaction rollback. " + ex,
                    TaskContext.class.getName(), LogUtil.LogLevelType.ERROR, rtid);
            return null;
        }
    }

    /**
     * Get task global unique id.
     * @return global id, this is NOT defined in BOXML but generated at runtime.
     */
    public String getTaskGlobalId() {
        return this.taskGlobalId;
    }

    /**
     * Get documentation text.
     * @return documentation string
     */
    public String getDocumentation() {
        return this.documentation;
    }

    /**
     * Get the unique id.
     * @return id string
     */
    public String getTaskId() {
        return this.taskId;
    }

    /**
     * Get task polymorphism name.
     * @return name string
     */
    public String getTaskName() {
        return this.taskName;
    }

    /**
     * Get the global id of BO which this task belong to.
     * @return BO global id string
     */
    public String getBoid() {
        return this.boid;
    }

    /**
     * Get the global id of Process which this task belong to.
     * @return Process global id string
     */
    public String getPid() {
        return this.pid;
    }

    /**
     * Get the resourcing principle.
     * @return principle string
     */
    public String getPrinciple() {
        return this.principle;
    }

    /**
     * Get the notification hooks dictionary.
     * @return HashMap of (ChangedName-NotifyURL)
     */
    public HashMap<String, String> getNotifyHooks() {
        return this.hooks;
    }

    /**
     * Get the callback events dictionary.
     * @return HashMap of (Status-EventName)
     */
    public HashMap<String, String> getCallbackEvents() {
        return this.callbacks;
    }

    /**
     * Get the parameter vector.
     * @return ArrayList of parameter name
     */
    public ArrayList<String> getParameters() {
        return this.parameters;
    }

    /**
     * Parse hooks by a descriptor in steady.
     * @param hookJSONDescriptor JSON descriptor
     */
    @SuppressWarnings("unchecked")
    private void ParseHooks(String hookJSONDescriptor) {
        this.hooks = SerializationUtil.JsonDeserialization(hookJSONDescriptor, HashMap.class);
    }

    /**
     * Parse callback events by a descriptor in steady.
     * @param callbackJSONDescriptor JSON descriptor
     */
    @SuppressWarnings("unchecked")
    private void ParseCallbacks(String callbackJSONDescriptor) {
        this.hooks = SerializationUtil.JsonDeserialization(callbackJSONDescriptor, HashMap.class);
    }

    /**
     * Parse parameter vector by a descriptor in steady.
     * @param parametersDescriptor parameter string descriptor
     */
    @SuppressWarnings("unchecked")
    private void ParseParameters(String parametersDescriptor) {
        String[] paras = parametersDescriptor.split(",");
        this.parameters = new ArrayList<>();
        this.parameters.addAll(Arrays.asList(paras));
    }

    /**
     * Generate a task context by a steady entity.
     * @param rstaskEntity RS task entity
     * @param pid Belong to process global id
     * @return equivalent task context.
     */
    private static TaskContext GenerateTaskContext(RenRstaskEntity rstaskEntity, String pid) {
        assert rstaskEntity != null;
        TaskContext context = new TaskContext(rstaskEntity.getPolymorphismId(),
                rstaskEntity.getPolymorphismName(), pid, rstaskEntity.getBoid(),
                rstaskEntity.getPrinciple(), rstaskEntity.getDocumentation());
        String hookDescriptor = rstaskEntity.getHookdescriptor();
        if (!CommonUtil.IsNullOrEmpty(hookDescriptor)) {
            context.ParseHooks(hookDescriptor);
        }
        String eventDescriptor = rstaskEntity.getEventdescriptor();
        if (!CommonUtil.IsNullOrEmpty(eventDescriptor)) {
            context.ParseCallbacks(eventDescriptor);
        }
        String parametersDescriptor = rstaskEntity.getParameters();
        if (!CommonUtil.IsNullOrEmpty(parametersDescriptor)) {
            context.ParseParameters(parametersDescriptor);
        }
        return context;
    }

    /**
     * Create a new context.
     * Private constructor for preventing create context without using `{@code TaskContext.GetContext}`.
     * @param id task unique id
     * @param name task name
     * @param pid belong to Process global id
     * @param boid belong to BO global id
     * @param principle resourcing principle
     * @param documentation task documentation text
     */
    private TaskContext(String id, String name, String pid, String boid, String principle, String documentation) {
        this.taskId = id;
        this.taskName = name;
        this.pid = pid;
        this.boid = boid;
        this.principle = principle;
        this.documentation = documentation;
    }
}