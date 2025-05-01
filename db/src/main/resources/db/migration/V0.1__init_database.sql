CREATE TABLE IF NOT EXISTS `posts` (

    `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
    `content` text

)ENGINE=InnoDB DEFAULT CHARSET=UTF8;

insert into posts (content) 
values 
('Hello world!'), 
('Hello world 2!'), 
('Hello world 3!'), 
('Hello world 4!'), 
('Hello world 5!'), 
('Hello world 6!'), 
('Hello world 7!'), 
('Hello world 8!'), 
('Hello world 9!'), 
('Hello world 10!');