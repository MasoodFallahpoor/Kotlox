grammar Lox;

// Currenly STRING is missing from the 'primary' rule

expression:
    equality (op+=',' equality)* ;

equality:
    comparison (op+=('!=' | '==') comparison)* ;

comparison:
    term (op+=( '>' | '>=' | '<' | '<=') term)* ;

term:
    factor (op+=('-' | '+') factor)* ;

factor:
    unary (op+=( '/' | '*') unary)* ;

unary:
    op+=('!' | '-') unary
    | primary ;

primary:
    NUMBER
    | 'true'
    | 'false'
    | 'nil'
    | '(' expression ')' ;

NUMBER: [0-9]+ ('.' [0-9]+)? ;
WS : [ \t\r\n]+ -> skip ;

OP_BANG: '!';
OP_PLUS: '+';
OP_MINUS: '-';
OP_STAR: '*';
OP_SLASH: '/';
OP_EQUAL: '==';
OP_INEQUAL: '!=';
OP_GREATER: '>';
OP_LESS: '<';
OP_GREATER_EQUAL: '>=';
OP_LESS_EQUAL: '<=';