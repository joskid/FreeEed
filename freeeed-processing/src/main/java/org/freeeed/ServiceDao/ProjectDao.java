package org.freeeed.ServiceDao;

import org.freeeed.Entity.Project;
import org.freeeed.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.Timestamp;
import java.util.List;
import java.util.TimeZone;

public class ProjectDao {


    private static volatile ProjectDao mInstance;
    private Session currentSession;

    private ProjectDao() {
        currentSession = HibernateUtil.
                getInstance().
                getSessionFactory()
                .withOptions()
                .jdbcTimeZone(TimeZone.getTimeZone("UTC"))
                .openSession();
    }

    public static ProjectDao getInstance() {
        if (mInstance == null) {
            synchronized (ProjectDao.class) {
                if (mInstance == null) {
                    mInstance = new ProjectDao();
                }
            }
        }
        return mInstance;
    }

    public int createProject(Project project) {
        Transaction transaction = currentSession.beginTransaction();
        project.setCreateTime(new Timestamp(System.currentTimeMillis()));
        currentSession.save(project);
        //int projectId = project.getProjectId();
        transaction.commit();
        return 1;
    }

    public List<Project> getProjects() {
        return currentSession.createQuery("from Project ").list();
    }

    public Project getProject(int projectId) {
        return currentSession.load(Project.class, projectId);
    }

    public void deleteProject(Project prj) {
        Transaction transaction = currentSession.beginTransaction();
        currentSession.delete(prj);
        transaction.commit();
    }


}