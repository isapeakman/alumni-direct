create table category
(
    id            int auto_increment
        primary key,
    category_name varchar(255)                       not null comment '分类名',
    description   text                               null comment '分类描述',
    parent_id     int      default 0                 null comment '上级id',
    update_time   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    create_time   datetime default CURRENT_TIMESTAMP not null
)
    comment '职位分类表';

create table chat_message
(
    id                 int auto_increment
        primary key,
    session_id         varchar(36)       not null comment '会话id',
    message_type       tinyint default 0 not null comment '消息类型0文本 1文件',
    message_content    varchar(500)      null comment '消息内容',
    send_user_id       int               not null comment '发送人ID',
    send_user_nickname varchar(20)       null comment '发送人昵称',
    send_time          datetime          not null comment '发送时间',
    contact_id         int               not null comment '接收人ID',
    file_size          int               null comment '文件大小',
    file_name          varchar(200)      null comment '文件名',
    file_type          tinyint           null comment '文件类型',
    status             tinyint default 1 null comment '状态 0正在发送 1 已发送'
);

create index chat_message__sessionId_index
    on chat_message (session_id);

create table chat_session
(
    session_id        varchar(36)  not null comment '会话ID'
        primary key,
    last_message      varchar(500) null comment '最后的信息',
    last_receive_time datetime     null comment '上次接收时间',
    job_id            int          null comment '当前会话求职的职位'
)
    comment '会话';

create table chat_user_session
(
    user_id    int         not null comment '发起人ID',
    contact_id int         not null comment '联系人ID',
    session_id varchar(36) null comment '会话ID',
    primary key (user_id, contact_id)
)
    comment '用户会话表';

create index chat_user_session_session_id_index
    on chat_user_session (session_id);

create table company
(
    id              int auto_increment comment '企业id'
        primary key,
    company_name    varchar(255)                       not null comment '公司名',
    company_address text                               null comment '公司地址',
    company_desc    text                               null comment '公司描述',
    website         text                               null comment '公司官网',
    update_time     datetime default CURRENT_TIMESTAMP not null
)
    comment '公司表';

create table education_detail
(
    id         int auto_increment
        primary key,
    user_id    int          not null comment '用户id',
    degree     tinyint      null comment '学历,0 本科 1 硕士 2 博士',
    school     varchar(255) null comment '毕业院校',
    major      varchar(50)  null comment '专业',
    start_time date         null comment '入学时间,yyyy-mm-dd',
    end_time   date         null comment '毕业时间,yyyy-mm-dd',
    constraint user_degree_school_major_uindex
        unique (user_id, degree, school, major)
)
    comment '学历详情表';

create table job
(
    id             int auto_increment
        primary key,
    title          varchar(255)                       not null comment '职位名称',
    job_type       tinyint                            null comment '职位类型: 0全职、1实习、2兼职',
    job_desc       text                               null comment '职位描述',
    location       text                               null comment '工作地点',
    min_salary     int                                null comment '最低薪资，单位K',
    max_salary     int                                null comment '最高薪资，单位K',
    company_name   varchar(255)                       null comment '企业名称',
    create_id      int                                null comment '创建用户',
    recruiter_name varchar(255)                       not null comment '招聘者名',
    is_alumni      tinyint  default 0                 null comment '是否是校友发布的, 0 否 1 是',
    status         tinyint                            not null comment '状态  0 待审批 1待发布 2 已发布 3 关闭 4审核未通过',
    create_time    datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time    datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    publish_time   datetime                           null comment '发布时间',
    salary_range   varchar(255)                       null
);

create table job_apply_record
(
    id           int auto_increment
        primary key,
    job_id       int                                null comment '职位id',
    applicant_id int                                null comment '申请人id',
    resume       varchar(255)                       null comment '简历附件',
    apply_time   datetime default CURRENT_TIMESTAMP not null comment '申请时间',
    constraint job_apply_record_pk
        unique (job_id, applicant_id)
)
    comment '职位申请表';

create index job_apply_record_applicant_id_index
    on job_apply_record (applicant_id);

create table job_approval_record
(
    id               int auto_increment
        primary key,
    job_id           int                                null comment '职位id',
    approval_user_id int                                null comment '审批人id',
    note             text                               null comment '描述信息',
    approval_status  tinyint  default 0                 null comment '审批状态 0 未审批 1已审批',
    approval_result  tinyint                            null comment '审批结果 0 未通过 1通过',
    approval_time    datetime                           null on update CURRENT_TIMESTAMP comment '审批时间',
    create_id        int                                null comment '发起审批人id',
    title            varchar(255)                       null comment '职位名称(冗余)',
    job_type         tinyint                            null comment '职位类型(冗余)',
    job_desc         text                               null comment '职位描述(冗余)',
    location         varchar(255)                       null comment '地点',
    salary_range     varchar(255)                       null comment '薪资范围(冗余)',
    company_name     varchar(255)                       null comment '公司名(冗余)',
    update_time      datetime                           null on update CURRENT_TIMESTAMP comment '更新时间',
    create_time      datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '职位审批表';

create table job_category
(
    id          int auto_increment
        primary key,
    job_id      int not null comment '职位id',
    category_id int not null comment '分类Id'
)
    comment '职位-分类-中间表';

create table job_fair
(
    id           int auto_increment comment '主键'
        primary key,
    type         tinyint                            null comment '类型: 0招聘会，1宣讲会',
    organizer    varchar(255)                       null comment '主办方',
    company      varchar(255)                       null comment '招聘公司',
    start_time   datetime                           not null comment '开始时间',
    end_time     datetime                           not null,
    location     text                               null comment 'text',
    contact_name varchar(50)                        null comment '联系人',
    contact_way  varchar(255)                       null comment '联系方式',
    image_url    varchar(500)                       null comment '图片',
    create_time  datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    create_id    int                                null comment '创建人',
    recruiter_id int                                null comment '招聘者',
    description  text                               null,
    name         varchar(255)                       null comment '招聘会或宣讲会名称'
)
    comment '招聘会/宣讲会';

create table user
(
    user_id         int auto_increment comment '用户Id'
        primary key,
    user_account    varchar(255)                       not null comment '用户账号，通常为邮箱地址',
    user_password   varchar(255)                       not null comment '用户密码',
    nickname        varchar(50)                        not null,
    user_avatar     varchar(255)                       null comment '头像地址',
    role            tinyint  default 1                 not null comment '0:admin; 1:user; ',
    company_id      int                                null comment '所属公司id',
    gender          tinyint  default 2                 null comment '性别：0 男 1 女 2 未知',
    birth           date                               null comment '出生年月',
    introduction    text                               null comment '个人介绍',
    create_time     datetime default CURRENT_TIMESTAMP not null,
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP,
    is_delete       tinyint  default 0                 not null comment '0 未删除 1已删除',
    last_login_time datetime                           null comment '上次登录时间',
    is_alumni       tinyint  default 0                 null comment '是否校友:0 否 1 是',
    status          int      default 0                 null comment '0 正常 1 禁用',
    constraint user_pk
        unique (user_account)
)
    comment '用户表';

create table user_certification_approval
(
    id               int auto_increment comment '主键'
        primary key,
    user_id          int                                null comment '申请人ID',
    certification    varchar(500)                       null comment '认证材料',
    certification2   varchar(500)                       null,
    apply_time       datetime                           null comment '申请时间',
    approval_user_id int                                null comment '审核人ID',
    approval_status  tinyint  default 0                 null comment '审批状态:0未审批 1 已审批',
    approval_result  tinyint                            null comment '审批结果 0 未通过 1 通过',
    note             text                               null comment '审批意见',
    approval_time    datetime                           null on update CURRENT_TIMESTAMP comment '审批时间',
    create_time      datetime default CURRENT_TIMESTAMP null comment '创建时间',
    update_time      datetime                           null on update CURRENT_TIMESTAMP comment '更新时间',
    name             varchar(50)                        null comment '真实姓名',
    student_id       varchar(50)                        null comment '学号',
    year_admission   date                               null,
    year_graduated   date                               null comment '毕业年份',
    major            varchar(50)                        null,
    phone            char(11)                           null
)
    comment '用户校友认证审批表';

create table user_intention
(
    id          int auto_increment comment '主键'
        primary key,
    user_id     int                                not null comment '用户ID',
    type        tinyint                            not null comment '职位类型 0 全职 1 实习 2 兼职',
    category_id varchar(500)                       not null comment '职位分类ID, 用逗号分隔',
    min_salary  int                                not null comment '最低薪资,单位k',
    max_salary  int                                not null comment '最高薪资,单位K',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间'
)
    comment '用户求职意愿表';

create index user_index
    on user_intention (user_id);

-- ==================== API调用监控相关表 ====================

-- API调用耗时日志表
create table api_call_log
(
    id              bigint auto_increment
        primary key,
    service_name    varchar(100)                       not null comment '服务名称（如GLM:BaiduOcrServiceImpl）',
    method_name     varchar(100)                       not null comment '方法名称',
    duration        bigint                             not null comment '耗时（毫秒）',
    success         tinyint(1)                         not null comment '是否成功：0 失败 1 成功',
    error_message   varchar(500)                       null comment '错误信息',
    request_params  text                               null comment '请求参数',
    response_length int                                null comment '响应长度',
    created_at      datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    index idx_created_at (created_at),
    index idx_service_name (service_name)
)
    comment 'API调用耗时日志表';

-- GLM Token消耗日志表
create table glm_token_log
(
    id                bigint auto_increment
        primary key,
    api_call_log_id   bigint                             null comment '关联的API调用日志ID',
    prompt_tokens     int                                not null comment '输入Token数',
    completion_tokens int                                not null comment '输出Token数',
    total_tokens      int                                not null comment '总Token数',
    model             varchar(50)                        not null comment '模型名称',
    created_at        datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment 'GLM Token消耗日志表';

