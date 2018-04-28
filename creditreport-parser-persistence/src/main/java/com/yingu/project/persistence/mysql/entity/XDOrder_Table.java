package com.yingu.project.persistence.mysql.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;
@Entity
@Getter
@Setter
public class XDOrder_Table {
    @Id
    private Integer id;

    private String ordernum;

    private Date ordertime;

    private Integer proid;

    private String proname;

    private Integer userid;

    private String username;

    private Boolean family;

    private Integer marid;

    private String marname;

    private BigDecimal rate;

    private Boolean zhengxin;

    private String orderstate;

    private BigDecimal loan;

    private BigDecimal actualoan;

    private String loantime;

    private String salechannel;

    private Boolean urgent;

    private String fuyi;

    private String loanuse;

    private String rufundway;

    private String salesname;

    private Integer salesid;

    private String usecard;

    private String rollbackstate;

    private Boolean isphoto;

    private String stated;

    private Integer adminId;

    private String adminNickname;

    private BigDecimal xdextendloan;

    private String xdextendtime;

    private String xdextendrate;

    private Integer xdproid;

    private String iscommonloan;

    private Integer quitreason;

    private Date lastoperationtime;

    private Integer ordertype;

    private Integer isonline;

    private String bindingid;

    private byte[] timestamp;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOrdernum() {
        return ordernum;
    }

    public void setOrdernum(String ordernum) {
        this.ordernum = ordernum == null ? null : ordernum.trim();
    }

    public Date getOrdertime() {
        return ordertime;
    }

    public void setOrdertime(Date ordertime) {
        this.ordertime = ordertime;
    }

    public Integer getProid() {
        return proid;
    }

    public void setProid(Integer proid) {
        this.proid = proid;
    }

    public String getProname() {
        return proname;
    }

    public void setProname(String proname) {
        this.proname = proname == null ? null : proname.trim();
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public Boolean getFamily() {
        return family;
    }

    public void setFamily(Boolean family) {
        this.family = family;
    }

    public Integer getMarid() {
        return marid;
    }

    public void setMarid(Integer marid) {
        this.marid = marid;
    }

    public String getMarname() {
        return marname;
    }

    public void setMarname(String marname) {
        this.marname = marname == null ? null : marname.trim();
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate;
    }

    public Boolean getZhengxin() {
        return zhengxin;
    }

    public void setZhengxin(Boolean zhengxin) {
        this.zhengxin = zhengxin;
    }

    public String getOrderstate() {
        return orderstate;
    }

    public void setOrderstate(String orderstate) {
        this.orderstate = orderstate == null ? null : orderstate.trim();
    }

    public BigDecimal getLoan() {
        return loan;
    }

    public void setLoan(BigDecimal loan) {
        this.loan = loan;
    }

    public BigDecimal getActualoan() {
        return actualoan;
    }

    public void setActualoan(BigDecimal actualoan) {
        this.actualoan = actualoan;
    }

    public String getLoantime() {
        return loantime;
    }

    public void setLoantime(String loantime) {
        this.loantime = loantime == null ? null : loantime.trim();
    }

    public String getSalechannel() {
        return salechannel;
    }

    public void setSalechannel(String salechannel) {
        this.salechannel = salechannel == null ? null : salechannel.trim();
    }

    public Boolean getUrgent() {
        return urgent;
    }

    public void setUrgent(Boolean urgent) {
        this.urgent = urgent;
    }

    public String getFuyi() {
        return fuyi;
    }

    public void setFuyi(String fuyi) {
        this.fuyi = fuyi == null ? null : fuyi.trim();
    }

    public String getLoanuse() {
        return loanuse;
    }

    public void setLoanuse(String loanuse) {
        this.loanuse = loanuse == null ? null : loanuse.trim();
    }

    public String getRufundway() {
        return rufundway;
    }

    public void setRufundway(String rufundway) {
        this.rufundway = rufundway == null ? null : rufundway.trim();
    }

    public String getSalesname() {
        return salesname;
    }

    public void setSalesname(String salesname) {
        this.salesname = salesname == null ? null : salesname.trim();
    }

    public Integer getSalesid() {
        return salesid;
    }

    public void setSalesid(Integer salesid) {
        this.salesid = salesid;
    }

    public String getUsecard() {
        return usecard;
    }

    public void setUsecard(String usecard) {
        this.usecard = usecard == null ? null : usecard.trim();
    }

    public String getRollbackstate() {
        return rollbackstate;
    }

    public void setRollbackstate(String rollbackstate) {
        this.rollbackstate = rollbackstate == null ? null : rollbackstate.trim();
    }

    public Boolean getIsphoto() {
        return isphoto;
    }

    public void setIsphoto(Boolean isphoto) {
        this.isphoto = isphoto;
    }

    public String getStated() {
        return stated;
    }

    public void setStated(String stated) {
        this.stated = stated == null ? null : stated.trim();
    }

    public Integer getAdminId() {
        return adminId;
    }

    public void setAdminId(Integer adminId) {
        this.adminId = adminId;
    }

    public String getAdminNickname() {
        return adminNickname;
    }

    public void setAdminNickname(String adminNickname) {
        this.adminNickname = adminNickname == null ? null : adminNickname.trim();
    }

    public BigDecimal getXdextendloan() {
        return xdextendloan;
    }

    public void setXdextendloan(BigDecimal xdextendloan) {
        this.xdextendloan = xdextendloan;
    }

    public String getXdextendtime() {
        return xdextendtime;
    }

    public void setXdextendtime(String xdextendtime) {
        this.xdextendtime = xdextendtime == null ? null : xdextendtime.trim();
    }

    public String getXdextendrate() {
        return xdextendrate;
    }

    public void setXdextendrate(String xdextendrate) {
        this.xdextendrate = xdextendrate == null ? null : xdextendrate.trim();
    }

    public Integer getXdproid() {
        return xdproid;
    }

    public void setXdproid(Integer xdproid) {
        this.xdproid = xdproid;
    }

    public String getIscommonloan() {
        return iscommonloan;
    }

    public void setIscommonloan(String iscommonloan) {
        this.iscommonloan = iscommonloan == null ? null : iscommonloan.trim();
    }

    public Integer getQuitreason() {
        return quitreason;
    }

    public void setQuitreason(Integer quitreason) {
        this.quitreason = quitreason;
    }

    public Date getLastoperationtime() {
        return lastoperationtime;
    }

    public void setLastoperationtime(Date lastoperationtime) {
        this.lastoperationtime = lastoperationtime;
    }

    public Integer getOrdertype() {
        return ordertype;
    }

    public void setOrdertype(Integer ordertype) {
        this.ordertype = ordertype;
    }

    public Integer getIsonline() {
        return isonline;
    }

    public void setIsonline(Integer isonline) {
        this.isonline = isonline;
    }

    public String getBindingid() {
        return bindingid;
    }

    public void setBindingid(String bindingid) {
        this.bindingid = bindingid == null ? null : bindingid.trim();
    }

    public byte[] getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(byte[] timestamp) {
        this.timestamp = timestamp;
    }
}