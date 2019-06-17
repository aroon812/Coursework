% Test Network 2
% 
% Solution should be as follows:
% 
% Red edge sequences:
% [a,c,f,h,k]
% [b,d,g,i,l]
% [b,d,g,i,j,l]
% 
% Alternating edge sequences:
% [b,d,f,h,i,l]
% [a,f,h,i,l]
% [a,d,e,j,l]
% [b,d,e,j,l]
% 
% Matching edge/state sequences:
% [a,f,i,k]

state(a, green).
state(b, red).
state(c, red).
state(d, red).
state(e, green).
state(f, green).
state(g, green).
state(h, green).
state(i, green).
state(j, red).
state(k, green).
state(l, red).

start_state(a).
start_state(b).

end_state(k).
end_state(l).

trans(a, c, red).
trans(a, f, green).
trans(a, d, green).
trans(b, d, red).
trans(b, d, green).
trans(c, f, red).
trans(d, f, green).
trans(d, g, red).
trans(d, e, red).
trans(e, j, green).
trans(f, i, green).
trans(f, h, red).
trans(g, i, red).
trans(h, i, green).
trans(h, k, red).
trans(i, k, green).
trans(i, j, red).
trans(i, l, red).
trans(j, l, red).
