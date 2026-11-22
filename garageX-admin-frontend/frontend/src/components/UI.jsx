import React from "react";
import { Search } from "lucide-react";
export function PageHead({title,subtitle,children}){return <div className="page-head"><div><h1>{title}</h1>{subtitle&&<p>{subtitle}</p>}</div><div className="head-actions">{children}</div></div>}
export function Modal({title,onClose,children,wide=false}){return <div className="modal-backdrop" onMouseDown={e=>e.target===e.currentTarget&&onClose()}><div className={"modal "+(wide?"wide":"")}><div className="modal-head"><h2>{title}</h2><button className="icon-btn" onClick={onClose}>×</button></div>{children}</div></div>}
export function Field({label,...p}){return <label className="field"><span>{label}</span><input {...p}/></label>}
export function Select({label,children,...p}){return <label className="field"><span>{label}</span><select {...p}>{children}</select></label>}
export function Textarea({label,...p}){return <label className="field"><span>{label}</span><textarea {...p}/></label>}
export function Badge({children,type=""}){return <span className={"badge "+type.toLowerCase().replaceAll("_","-")}>{children}</span>}
export function Empty({text="No records found"}){return <div className="empty"><div>◎</div><b>{text}</b><span>Data from your GarejX backend will appear here.</span></div>}
export function ErrorBox({error}){return error?<div className="error-box">{error}</div>:null}
export function SearchBox({value,onChange,placeholder="Search..."}){return <div className="searchbox"><Search size={17}/><input value={value} onChange={e=>onChange(e.target.value)} placeholder={placeholder}/></div>}
